package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableRepresentativeDto;
import com.mav.openzev.api.model.RepresentativeDto;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.RepresentativeModels;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class RepresentativeApiIntegrationTest extends AbstractApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @Nested
  class GetRepresentativesTests {

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(RepresentativeModels.getRepresentative());

      // act
      final ResponseEntity<RepresentativeDto[]> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              RepresentativeDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class GetRepresentativeTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(RepresentativeModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("representative_not_found", ErrorDto::getCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(RepresentativeModels.getRepresentative());

      // act
      final ResponseEntity<RepresentativeDto> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(RepresentativeModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              RepresentativeDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(RepresentativeModels.UUID, RepresentativeDto::getId)
                      .returns(RepresentativeModels.ANNA, RepresentativeDto::getFirstName)
                      .returns(RepresentativeModels.BARRY, RepresentativeDto::getLastName)
                      .returns(RepresentativeModels.STRADUN, RepresentativeDto::getStreet)
                      .returns(RepresentativeModels.ANNA_BARRY_COM, RepresentativeDto::getEmail)
                      .returns(RepresentativeModels._30, RepresentativeDto::getHouseNr)
                      .returns(RepresentativeModels._1624, RepresentativeDto::getPostalCode)
                      .returns(RepresentativeModels.GRATTAVACHE, RepresentativeDto::getCity)
                      .returns(RepresentativeModels._41415556677, RepresentativeDto::getPhoneNr)
                      .returns(RepresentativeModels._41795556677, RepresentativeDto::getMobileNr));
    }
  }

  @Nested
  class CreateRepresentativeTests {

    @ParameterizedTest
    @RequiredSource(ModifiableRepresentativeDto.class)
    void status400(final ModifiableRepresentativeDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(RepresentativeModels.getRepresentative());

      final ModifiableRepresentativeDto requestBody =
          new ModifiableRepresentativeDto()
              .firstName(RepresentativeModels.ANNA)
              .lastName(RepresentativeModels.BARRY)
              .email(RepresentativeModels.ANNA_BARRY_COM)
              .street(RepresentativeModels.STRADUN)
              .houseNr(RepresentativeModels._30)
              .postalCode(RepresentativeModels._1624)
              .city(RepresentativeModels.GRATTAVACHE)
              .phoneNr(RepresentativeModels._41415556677)
              .mobileNr(RepresentativeModels._41795556677);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.settings_representatives(),
                  HttpMethod.GET,
                  new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
                  RepresentativeDto[].class)
              .getBody());
    }
  }

  @Nested
  class ChangeRepresentativeTests {

    @ParameterizedTest
    @RequiredSource(ModifiableRepresentativeDto.class)
    void status400(final ModifiableRepresentativeDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(RepresentativeModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(
          RepresentativeModels.getRepresentative().toBuilder().uuid(UUID.randomUUID()).build());
      testDatabaseService.insert(RepresentativeModels.getRepresentative());

      final ModifiableRepresentativeDto requestBody =
          new ModifiableRepresentativeDto()
              .firstName(RepresentativeModels.ANNA)
              .lastName(RepresentativeModels.BARRY)
              .email(RepresentativeModels.ANNA_BARRY_COM)
              .street(RepresentativeModels.STRADUN)
              .houseNr(RepresentativeModels._30)
              .postalCode(RepresentativeModels._1624)
              .city(RepresentativeModels.GRATTAVACHE)
              .phoneNr(RepresentativeModels._41415556677)
              .mobileNr(RepresentativeModels._41795556677);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(RepresentativeModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.settings_representatives(),
                  HttpMethod.GET,
                  new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
                  RepresentativeDto[].class)
              .getBody());
    }
  }

  @Nested
  class DeleteRepresentativeTests {
    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(RepresentativeModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status204() {
      // arrange
      testDatabaseService.insert(RepresentativeModels.getRepresentative());

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.settings_representatives(RepresentativeModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
