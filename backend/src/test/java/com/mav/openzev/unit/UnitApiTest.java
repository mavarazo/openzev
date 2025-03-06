package com.mav.openzev.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.AbstractApiIntegrationTest;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.UnitDto;
import com.mav.openzev.unit.entity.Unit;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UnitApiTest extends AbstractApiIntegrationTest {

  private static final String V_1_UNITS = "/v1/units";
  private static final String V_1_UNITS_ID = "/v1/units/{unitId}";

  @Autowired private UnitTestDataService unitTestDataService;

  @Nested
  class GetUnitsTests {

    @Test
    void status200() {
      // arrange
      unitTestDataService.newUnit(u -> u.number("1234"));
      unitTestDataService.newUnit(u -> u.number("5678"));

      // act
      final ResponseEntity<UnitDto[]> response =
          restTemplate.exchange(V_1_UNITS, HttpMethod.GET, HttpEntity.EMPTY, UnitDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .hasSize(2)
                      .extracting(UnitDto::getNumber)
                      .containsExactly("1234", "5678"));
    }
  }

  @Nested
  class CreateUnitTests {

    @Test
    void status204() {
      // arrange
      final UnitDto unitDto = new UnitDto().number("1234").firstName("Foo").lastName("Bar");

      // act
      final ResponseEntity<UnitDto> response =
          restTemplate.exchange(
              V_1_UNITS, HttpMethod.POST, new HttpEntity<>(unitDto, null), UnitDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("1234", UnitDto::getNumber)
                      .returns("Foo", UnitDto::getFirstName)
                      .returns("Bar", UnitDto::getLastName));
    }
  }

  @Nested
  class GetUnitTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();

      // act
      final ResponseEntity<UnitDto> response =
          restTemplate.exchange(
              V_1_UNITS_ID, HttpMethod.GET, HttpEntity.EMPTY, UnitDto.class, unit.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("1234", UnitDto::getNumber)
                      .returns("Foo", UnitDto::getFirstName)
                      .returns("Bar", UnitDto::getLastName));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_UNITS_ID, HttpMethod.GET, HttpEntity.EMPTY, ErrorDto.class, UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class ChangeUnitTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final UnitDto unitDto = new UnitDto().number("5678").firstName("John").lastName("Doe");

      // act
      final ResponseEntity<UnitDto> response =
          restTemplate.exchange(
              V_1_UNITS_ID, HttpMethod.PUT, new HttpEntity<>(unitDto), UnitDto.class, unit.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("5678", UnitDto::getNumber)
                      .returns("John", UnitDto::getFirstName)
                      .returns("Doe", UnitDto::getLastName));
    }

    @Test
    void status404() {
      // arrange
      final UnitDto unitDto = new UnitDto().number("5678").firstName("John").lastName("Doe");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_UNITS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(unitDto),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class DeleteMeterPointTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();

      // act
      final ResponseEntity<UnitDto> response =
          restTemplate.exchange(
              V_1_UNITS_ID, HttpMethod.DELETE, HttpEntity.EMPTY, UnitDto.class, unit.getId());

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_UNITS_ID, HttpMethod.DELETE, HttpEntity.EMPTY, ErrorDto.class, UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }
}
