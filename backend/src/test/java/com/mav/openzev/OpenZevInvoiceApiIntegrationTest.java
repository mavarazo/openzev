package com.mav.openzev;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.CreatableInvoiceDto;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.InvoiceDto;
import com.mav.openzev.api.model.UpdatableInvoiceDto;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.InvoiceRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
public class OpenZevInvoiceApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private InvoiceRepository invoiceRepository;
  @Autowired private AccountingRepository accountingRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetInvoiceTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices("3394bcab-a426-45dd-8038-6ad347c1dd3b"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_not_found", ErrorDto::getCode)
          .returns(
              "invoice with id '3394bcab-a426-45dd-8038-6ad347c1dd3b' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/agreements.sql",
          "/db/test-data/accountings.sql",
          "/db/test-data/invoices.sql"
        })
    void status200() {
      // act
      final ResponseEntity<InvoiceDto> response =
          restTemplate.exchange(
              UriFactory.invoices("3394bcab-a426-45dd-8038-6ad347c1dd3b"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              InvoiceDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(1000.00, InvoiceDto::getUsageHighTariff)
                      .returns(750.00, InvoiceDto::getUsageLowTariff)
                      .returns(1750.00, InvoiceDto::getUsageTotal)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(100), InvoiceDto::getAmountHighTariff)
                      .returns(BigDecimal.valueOf(75), InvoiceDto::getAmountLowTariff)
                      .returns(BigDecimal.valueOf(175), InvoiceDto::getAmountTotal)
                      .returns(LocalDate.of(2023, 6, 1), InvoiceDto::getPayed));
    }
  }

  @Nested
  class CreateInvoiceTests {

    @ParameterizedTest
    @CsvSource(
        value = {"089a394c-4d25-4329-b696-7b6ab93b03b2,", ",089a394c-4d25-4329-b696-7b6ab93b03b2"})
    void status400(final String accountingId, final String unitId) {
      // arrange
      final CreatableInvoiceDto requestBody =
          new CreatableInvoiceDto()
              .accountingId(nonNull(accountingId) ? UUID.fromString(accountingId) : null)
              .unitId(nonNull(unitId) ? UUID.fromString(unitId) : null);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/agreements.sql",
          "/db/test-data/accountings.sql"
        })
    void status201() {
      // arrange
      final CreatableInvoiceDto requestBody =
          new CreatableInvoiceDto()
              .accountingId(UUID.fromString("86fb361f-a577-405e-af02-f524478d2e49"))
              .unitId(UUID.fromString("414d2033-3b17-4e68-b69e-e483db0dc90b"))
              .usageHighTariff(1000.00)
              .usageLowTariff(750.00)
              .usageTotal(1750.00)
              .amountHighTariff(BigDecimal.valueOf(100))
              .amountLowTariff(BigDecimal.valueOf(75))
              .amountTotal(BigDecimal.valueOf(175))
              .payed(LocalDate.of(2023, 6, 1));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.invoices(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(invoiceRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              invoice ->
                  assertThat(invoice)
                      .returns(1000.00, Invoice::getUsageHighTariff)
                      .returns(750.00, Invoice::getUsageLowTariff)
                      .returns(1750.00, Invoice::getUsageTotal)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(100), Invoice::getAmountHighTariff)
                      .returns(BigDecimal.valueOf(75), Invoice::getAmountLowTariff)
                      .returns(BigDecimal.valueOf(175), Invoice::getAmountTotal)
                      .returns(LocalDate.of(2023, 6, 1), Invoice::getPayed));
    }
  }

  @Nested
  class ChangeInvoiceTests {

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/agreements.sql",
          "/db/test-data/accountings.sql"
        })
    void status404() {
      // arrange
      final UpdatableInvoiceDto requestBody = new UpdatableInvoiceDto();

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices("3394bcab-a426-45dd-8038-6ad347c1dd3b"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_not_found", ErrorDto::getCode)
          .returns(
              "invoice with id '3394bcab-a426-45dd-8038-6ad347c1dd3b' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/agreements.sql",
          "/db/test-data/accountings.sql",
          "/db/test-data/invoices.sql"
        })
    void status200() {
      // arrange
      final UpdatableInvoiceDto requestBody =
          new UpdatableInvoiceDto()
              .usageHighTariff(1500.00)
              .usageLowTariff(500.00)
              .usageTotal(2000.00)
              .amountHighTariff(BigDecimal.valueOf(150))
              .amountLowTariff(BigDecimal.valueOf(50))
              .amountTotal(BigDecimal.valueOf(200))
              .payed(LocalDate.of(2023, 4, 1));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.invoices("3394bcab-a426-45dd-8038-6ad347c1dd3b"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(invoiceRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              invoice ->
                  assertThat(invoice)
                      .returns(1500.00, Invoice::getUsageHighTariff)
                      .returns(500.00, Invoice::getUsageLowTariff)
                      .returns(2000.00, Invoice::getUsageTotal)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(150), Invoice::getAmountHighTariff)
                      .returns(BigDecimal.valueOf(50), Invoice::getAmountLowTariff)
                      .returns(BigDecimal.valueOf(200), Invoice::getAmountTotal)
                      .returns(LocalDate.of(2023, 4, 1), Invoice::getPayed));
    }
  }

  @Nested
  class DeleteInvoiceTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices("3394bcab-a426-45dd-8038-6ad347c1dd3b"),
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
    void status204() {
      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.invoices("3394bcab-a426-45dd-8038-6ad347c1dd3b"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
