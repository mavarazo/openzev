package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.AgreementDto;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableAgreementDto;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.repository.AgreementRepository;
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
public class OpenZevAgreementApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private AgreementRepository agreementRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetAgreementsTests {

    @Test
    @Sql(scripts = {"/db/test-data/agreements.sql"})
    void status200() {
      // act
      final ResponseEntity<AgreementDto[]> response =
          restTemplate.exchange(
              UriFactory.agreements(), HttpMethod.GET, HttpEntity.EMPTY, AgreementDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetAgreementTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("agreement_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/agreements.sql"})
    void status200() {
      // act
      final ResponseEntity<AgreementDto> response =
          restTemplate.exchange(
              UriFactory.agreements("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              AgreementDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(
                          UUID.fromString("86fb361f-a577-405e-af02-f524478d2e49"),
                          AgreementDto::getId)
                      .returns(LocalDate.of(2023, 1, 1), AgreementDto::getPeriodFrom)
                      .returns(LocalDate.of(2023, 12, 31), AgreementDto::getPeriodUpto)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(0.20), AgreementDto::getHighTariff)
                      .returns(BigDecimal.valueOf(0.10), AgreementDto::getLowTariff)
                      .returns(LocalDate.of(2022, 12, 1), AgreementDto::getApproved));
    }
  }

  @Nested
  class CreateAgreementTests {

    @ParameterizedTest
    @CsvSource(
        value = {
          ",2023-12-31,2,1",
          "2023-01-01,,2,1",
          "2023-01-01,2023-12-31,,1",
          "2023-01-01,2023-12-31,2,"
        })
    void status400(
        final LocalDate periodFrom,
        final LocalDate periodUpto,
        final BigDecimal highTariff,
        final BigDecimal lowTariff) {
      // arrange
      final ModifiableAgreementDto requestBody =
          new ModifiableAgreementDto()
              .periodFrom(periodFrom)
              .periodUpto(periodUpto)
              .highTariff(highTariff)
              .lowTariff(lowTariff);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status201() {
      // arrange
      final ModifiableAgreementDto requestBody =
          new ModifiableAgreementDto()
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31))
              .highTariff(BigDecimal.valueOf(0.2))
              .lowTariff(BigDecimal.valueOf(0.1));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.agreements(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(agreementRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              agreement ->
                  assertThat(agreement)
                      .returns(LocalDate.of(2023, 1, 1), Agreement::getPeriodFrom)
                      .returns(LocalDate.of(2023, 12, 31), Agreement::getPeriodUpto)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(0.2), Agreement::getHighTariff)
                      .returns(BigDecimal.valueOf(0.1), Agreement::getLowTariff)
                      .returns(null, Agreement::getApproved));
    }
  }

  @Nested
  class ChangeAgreementTests {

    @ParameterizedTest
    @CsvSource(
        value = {
          ",2023-12-31,2,1",
          "2023-01-01,,2,1",
          "2023-01-01,2023-12-31,,1",
          "2023-01-01,2023-12-31,2,"
        })
    @Sql(scripts = {"/db/test-data/agreements.sql"})
    void status400(
        final LocalDate periodFrom,
        final LocalDate periodUpto,
        final BigDecimal highTariff,
        final BigDecimal lowTariff) {
      // arrange
      final ModifiableAgreementDto requestBody =
          new ModifiableAgreementDto()
              .periodFrom(periodFrom)
              .periodUpto(periodUpto)
              .highTariff(highTariff)
              .lowTariff(lowTariff);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiableAgreementDto requestBody =
          new ModifiableAgreementDto()
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31))
              .highTariff(BigDecimal.valueOf(0.2))
              .lowTariff(BigDecimal.valueOf(0.1))
              .approved(LocalDate.of(2022, 12, 1));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("agreement_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/agreements.sql"})
    void status200() {
      // arrange
      final ModifiableAgreementDto requestBody =
          new ModifiableAgreementDto()
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31))
              .highTariff(BigDecimal.valueOf(0.2))
              .lowTariff(BigDecimal.valueOf(0.1))
              .approved(LocalDate.of(2022, 12, 1));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.agreements("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(agreementRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              agreement ->
                  assertThat(agreement)
                      .returns(LocalDate.of(2023, 1, 1), Agreement::getPeriodFrom)
                      .returns(LocalDate.of(2023, 12, 31), Agreement::getPeriodUpto)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(0.2), Agreement::getHighTariff)
                      .returns(BigDecimal.valueOf(0.1), Agreement::getLowTariff)
                      .returns(LocalDate.of(2022, 12, 1), Agreement::getApproved));
    }
  }

  @Nested
  class DeleteAgreementTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("agreement_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/agreements.sql",
          "/db/test-data/accountings.sql",
        })
    void status422() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("agreement_has_accounting", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/agreements.sql"})
    void status204() {
      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.agreements("86fb361f-a577-405e-af02-f524478d2e49"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
