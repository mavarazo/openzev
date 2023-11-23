package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.AgreementDto;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableAgreementDto;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.AccountingModels;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.model.AgreementModels;
import com.mav.openzev.model.PropertyModels;
import com.mav.openzev.repository.AgreementRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenZevAgreementApiIntegrationTest {

  private static final LocalDate _2025_01_01 = LocalDate.of(2025, 1, 1);
  private static final LocalDate _2025_12_31 = LocalDate.of(2025, 12, 31);
  private static final BigDecimal _0_30 = BigDecimal.valueOf(0.30);
  private static final BigDecimal _0_20 = BigDecimal.valueOf(0.20);
  public static final LocalDate _2024_09_01 = LocalDate.of(2024, 9, 1);

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
    void status200() {
      // arrange
      testDatabaseService.insertProperty(
          PropertyModels.getProperty().addAgreement(AgreementModels.getAgreement()));

      // act
      final ResponseEntity<AgreementDto[]> response =
          restTemplate.exchange(
              UriFactory.properties_agreements(PropertyModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              AgreementDto[].class);

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
              UriFactory.agreements(AgreementModels.UUID),
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
    void status200() {
      // arrange
      testDatabaseService.insertProperty(
          PropertyModels.getProperty().addAgreement(AgreementModels.getAgreement()));

      // act
      final ResponseEntity<AgreementDto> response =
          restTemplate.exchange(
              UriFactory.agreements(AgreementModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              AgreementDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(AgreementModels.UUID, AgreementDto::getId)
                      .returns(AgreementModels._2024_01_01, AgreementDto::getPeriodFrom)
                      .returns(AgreementModels._2024_12_31, AgreementDto::getPeriodUpto)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(AgreementModels._0_25, AgreementDto::getHighTariff)
                      .returns(AgreementModels._0_15, AgreementDto::getLowTariff)
                      .returns(AgreementModels._2023_10_01, AgreementDto::getApproved));
    }
  }

  @Nested
  class CreateAgreementTests {

    @ParameterizedTest
    @RequiredSource(ModifiableAgreementDto.class)
    void status400(final ModifiableAgreementDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.properties_agreements(PropertyModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404_property_not_found() {
      // arrange
      final ModifiableAgreementDto requestBody =
          new ModifiableAgreementDto()
              .periodFrom(AgreementModels._2024_01_01)
              .periodUpto(AgreementModels._2024_12_31)
              .highTariff(AgreementModels._0_25)
              .lowTariff(AgreementModels._0_15);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.properties_agreements(PropertyModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("property_not_found", ErrorDto::getCode);
    }

    @Test
    void status201() {
      // arrange
      testDatabaseService.insertProperty(PropertyModels.getProperty());

      final ModifiableAgreementDto requestBody =
          new ModifiableAgreementDto()
              .periodFrom(AgreementModels._2024_01_01)
              .periodUpto(AgreementModels._2024_12_31)
              .highTariff(AgreementModels._0_25)
              .lowTariff(AgreementModels._0_15);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.properties_agreements(PropertyModels.UUID),
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
                      .returns(AgreementModels._2024_01_01, Agreement::getPeriodFrom)
                      .returns(AgreementModels._2024_12_31, Agreement::getPeriodUpto)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(AgreementModels._0_25, Agreement::getHighTariff)
                      .returns(AgreementModels._0_15, Agreement::getLowTariff)
                      .returns(null, Agreement::getApproved));
    }
  }

  @Nested
  class ChangeAgreementTests {

    @ParameterizedTest
    @RequiredSource(ModifiableAgreementDto.class)
    void status400(final ModifiableAgreementDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements(AgreementModels.UUID),
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
              .periodFrom(AgreementModels._2024_01_01)
              .periodUpto(AgreementModels._2024_01_01)
              .highTariff(BigDecimal.valueOf(0.2))
              .lowTariff(BigDecimal.valueOf(0.1))
              .approved(LocalDate.of(2022, 12, 1));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements(AgreementModels.UUID),
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
    void status200() {
      // arrange
      testDatabaseService.insertProperty(
          PropertyModels.getProperty().addAgreement(AgreementModels.getAgreement()));

      final ModifiableAgreementDto requestBody =
          new ModifiableAgreementDto()
              .periodFrom(_2025_01_01)
              .periodUpto(_2025_12_31)
              .highTariff(_0_30)
              .lowTariff(_0_20)
              .approved(_2024_09_01);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.agreements(AgreementModels.UUID),
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
                      .returns(_2025_01_01, Agreement::getPeriodFrom)
                      .returns(_2025_12_31, Agreement::getPeriodUpto)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(_0_30, Agreement::getHighTariff)
                      .returns(_0_20, Agreement::getLowTariff)
                      .returns(_2024_09_01, Agreement::getApproved));
    }
  }

  @Nested
  class DeleteAgreementTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements(AgreementModels.UUID),
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
    void status422() {
      // arrange
      final Agreement agreement = AgreementModels.getAgreement();
      testDatabaseService.insertProperty(
          PropertyModels.getProperty()
              .addAccounting(
                  AccountingModels.getAccounting().toBuilder().agreement(agreement).build())
              .addAgreement(agreement));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.agreements(AgreementModels.UUID),
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
    void status204() {
      // arrange
      testDatabaseService.insertProperty(
          PropertyModels.getProperty().addAgreement(AgreementModels.getAgreement()));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.agreements(AgreementModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
