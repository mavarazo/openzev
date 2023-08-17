package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.AccountingDto;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableAccountingDto;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.repository.AccountingRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenZevAccountingApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private com.mav.openzev.TestDatabaseService testDatabaseService;

  @Autowired private AccountingRepository accountingRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetAccountingsTests {

    @Test
    @Sql(scripts = {"/db/test-data/agreements.sql", "/db/test-data/accountings.sql"})
    void status200() {
      // act
      final ResponseEntity<AccountingDto[]> response =
          restTemplate.exchange(
              UriFactory.accountings(), HttpMethod.GET, HttpEntity.EMPTY, AccountingDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetAccountingTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_not_found", ErrorDto::getCode)
          .returns(
              "accounting with id '86fb361f-a577-405e-af02-f524478d2e49' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(scripts = {"/db/test-data/agreements.sql", "/db/test-data/accountings.sql"})
    void status200() {
      // act
      final ResponseEntity<AccountingDto> response =
          restTemplate.exchange(
              UriFactory.accountings("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              AccountingDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(
                          UUID.fromString("86fb361f-a577-405e-af02-f524478d2e49"),
                          AccountingDto::getUuid)
                      .returns(LocalDate.of(2023, 1, 1), AccountingDto::getPeriodFrom)
                      .returns(LocalDate.of(2023, 12, 31), AccountingDto::getPeriodUpto)
                      .returns("Abrechnung 2023", AccountingDto::getSubject)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(100), AccountingDto::getAmountHighTariff)
                      .returns(BigDecimal.valueOf(75.00), AccountingDto::getAmountLowTariff)
                      .returns(BigDecimal.valueOf(175.00), AccountingDto::getAmountTotal));
    }
  }

  @Nested
  class CreateAccountingTests {

    @Test
    void status201() {
      // arrange
      final ModifiableAccountingDto requestBody =
          new ModifiableAccountingDto()
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31))
              .subject("Abrechnung 2023")
              .amountHighTariff(BigDecimal.valueOf(100))
              .amountLowTariff(BigDecimal.valueOf(75))
              .amountTotal(BigDecimal.valueOf(175));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.accountings(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(accountingRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              accounting ->
                  assertThat(accounting)
                      .returns(LocalDate.of(2023, 1, 1), Accounting::getPeriodFrom)
                      .returns(LocalDate.of(2023, 12, 31), Accounting::getPeriodUpto)
                      .returns("Abrechnung 2023", Accounting::getSubject)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(100), Accounting::getAmountHighTariff)
                      .returns(BigDecimal.valueOf(75), Accounting::getAmountLowTariff)
                      .returns(BigDecimal.valueOf(175), Accounting::getAmountTotal));
    }
  }

  @Nested
  class ChangeAccountingTests {

    @Test
    void status404() {
      // arrange
      final ModifiableAccountingDto requestBody =
          new ModifiableAccountingDto()
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31))
              .subject("Abrechnung 2023")
              .amountHighTariff(BigDecimal.valueOf(100))
              .amountLowTariff(BigDecimal.valueOf(75))
              .amountTotal(BigDecimal.valueOf(175));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_not_found", ErrorDto::getCode)
          .returns(
              "accounting with id '86fb361f-a577-405e-af02-f524478d2e49' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(scripts = {"/db/test-data/agreements.sql", "/db/test-data/accountings.sql"})
    void status200() {
      // arrange
      final ModifiableAccountingDto requestBody =
          new ModifiableAccountingDto()
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 3, 31))
              .subject("Abrechnung 01/2023")
              .amountHighTariff(BigDecimal.valueOf(100))
              .amountLowTariff(BigDecimal.valueOf(75))
              .amountTotal(BigDecimal.valueOf(175));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.accountings("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(accountingRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              accounting ->
                  assertThat(accounting)
                      .returns(LocalDate.of(2023, 1, 1), Accounting::getPeriodFrom)
                      .returns(LocalDate.of(2023, 3, 31), Accounting::getPeriodUpto)
                      .returns("Abrechnung 01/2023", Accounting::getSubject)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(100), Accounting::getAmountHighTariff)
                      .returns(BigDecimal.valueOf(75), Accounting::getAmountLowTariff)
                      .returns(BigDecimal.valueOf(175), Accounting::getAmountTotal));
    }
  }

  @Nested
  class DeleteAccountingTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/agreements.sql",
          "/db/test-data/accountings.sql",
          "/db/test-data/invoices.sql",
        })
    void status422() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_has_invoice", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/agreements.sql",
          "/db/test-data/accountings.sql",
        })
    void status204() {
      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.accountings("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
