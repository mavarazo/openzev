package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableUnitDto;
import com.mav.openzev.api.model.UnitDto;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.UnitRepository;
import com.mav.openzev.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
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
public class OpenZevUnitApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private UserRepository userRepository;
  @Autowired private UnitRepository unitRepository;

  @AfterEach
  void tearDown() {
    unitRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Nested
  class GetUnitsTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("unit_not_found", ErrorDto::getCode)
          .returns(
              "unit with id '414d2033-3b17-4e68-b69e-e483db0dc90b' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(scripts = {"/db/test-data/units.sql"})
    void status200() {
      // act
      final ResponseEntity<UnitDto[]> response =
          restTemplate.exchange(
              UriFactory.units(), HttpMethod.GET, HttpEntity.EMPTY, UnitDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetUnitTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("unit_not_found", ErrorDto::getCode)
          .returns(
              "unit with id '414d2033-3b17-4e68-b69e-e483db0dc90b' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(scripts = {"/db/test-data/users.sql", "/db/test-data/units.sql"})
    void status200() {
      // act
      final ResponseEntity<UnitDto> response =
          restTemplate.exchange(
              UriFactory.units("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              UnitDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(
                          UUID.fromString("414d2033-3b17-4e68-b69e-e483db0dc90b"), UnitDto::getUuid)
                      .returns(true, UnitDto::getActive)
                      .returns("EG/1.OG rechts", UnitDto::getSubject)
                      .returns(125, UnitDto::getValueRatio)
                      .returns("e4e2043a-05b1-4735-a5ea-8f972115df17", UnitDto::getMpan));
    }
  }

  @Nested
  class CreateUnitTests {

    @ParameterizedTest
    @NullSource
    void status400(final String subject) {
      // arrange
      final ModifiableUnitDto requestBody = new ModifiableUnitDto().subject(subject);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status201() {
      // arrange
      final ModifiableUnitDto requestBody =
          new ModifiableUnitDto()
              .subject("EG/1.OG rechts")
              .valueRatio(125)
              .mpan("414d2033-3b17-4e68-b69e-e483db0dc90b");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.units(), HttpMethod.POST, new HttpEntity<>(requestBody, null), UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(unitRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              unit ->
                  assertThat(unit)
                      .returns(true, Unit::isActive)
                      .returns("EG/1.OG rechts", Unit::getSubject)
                      .returns(125, Unit::getValueRatio)
                      .returns(
                          "414d2033-3b17-4e68-b69e-e483db0dc90b",
                          Unit::getMeterPointAdministrationNumber));
    }
  }

  @Nested
  class ChangeUnitTests {

    @ParameterizedTest
    @NullSource
    @Sql(scripts = {"/db/test-data/units.sql"})
    void status400(final String subject) {
      // arrange
      final ModifiableUnitDto requestBody = new ModifiableUnitDto().subject(subject);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/users.sql"})
    void status404() {
      // arrange
      final ModifiableUnitDto requestBody = new ModifiableUnitDto().subject("EG/1.OG rechts");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("unit_not_found", ErrorDto::getCode)
          .returns(
              "unit with id '414d2033-3b17-4e68-b69e-e483db0dc90b' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(scripts = {"/db/test-data/users.sql", "/db/test-data/units.sql"})
    void status200() {
      // arrange
      final ModifiableUnitDto requestBody =
          new ModifiableUnitDto()
              .subject("EG/1.OG rechts")
              .valueRatio(55)
              .mpan("c113ad13-dbef-4936-8f84-29ce39cb2ab9");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.units("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(unitRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              unit ->
                  assertThat(unit)
                      .returns(
                          UUID.fromString("414d2033-3b17-4e68-b69e-e483db0dc90b"), Unit::getUuid)
                      .returns(true, Unit::isActive)
                      .returns("EG/1.OG rechts", Unit::getSubject)
                      .returns(55, Unit::getValueRatio)
                      .returns(
                          "c113ad13-dbef-4936-8f84-29ce39cb2ab9",
                          Unit::getMeterPointAdministrationNumber));
    }
  }
}
