package com.mav.openzev.meter_point;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.AbstractApiIntegrationTest;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.MeterPointDto;
import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.unit.UnitTestDataService;
import com.mav.openzev.unit.entity.Unit;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class MeterPointApiIntegrationTests extends AbstractApiIntegrationTest {

  private static final String V_1_METER_POINTS = "/v1/meter-points";
  private static final String V_1_METER_POINTS_ID = "/v1/meter-points/{meterPointId}";

  @Autowired private MeterPointTestDataService meterPointTestDataService;
  @Autowired private UnitTestDataService unitTestDataService;

  @Nested
  class GetMeterPointsTests {

    @Test
    void status200() {
      // arrange
      meterPointTestDataService.newMeterPoint(
          m -> m.unit(unitTestDataService.newUnit()).number("12 635 851"));
      meterPointTestDataService.newMeterPoint(
          m -> m.unit(unitTestDataService.newUnit()).number("34 635 851"));

      // act
      final ResponseEntity<MeterPointDto[]> response =
          restTemplate.exchange(
              V_1_METER_POINTS, HttpMethod.GET, HttpEntity.EMPTY, MeterPointDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .hasSize(2)
                      .extracting(MeterPointDto::getNumber)
                      .containsExactly("12 635 851", "34 635 851"));
    }
  }

  @Nested
  class CreateMeterPointTests {

    @Test
    void status204() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPointDto meterPointDto =
          new MeterPointDto().unitId(unit.getId()).number("12 635 851");

      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS,
              HttpMethod.POST,
              new HttpEntity<>(meterPointDto, null),
              MeterPointDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns("12 635 851", MeterPointDto::getNumber));
    }
  }

  @Nested
  class GetMeterPointTests {

    @Test
    void status200() {
      // arrange
      final MeterPoint meterPoint =
          meterPointTestDataService.newMeterPoint(
              m -> m.unit(unitTestDataService.newUnit()).number("12 635 851"));

      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              MeterPointDto.class,
              meterPoint.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns("12 635 851", MeterPointDto::getNumber));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class ChangeMeterPointTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint =
          meterPointTestDataService.newMeterPoint(m -> m.unit(unit).number("12 635 851"));

      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(new MeterPointDto().unitId(unit.getId()).number("12 345 678")),
              MeterPointDto.class,
              meterPoint.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns("12 345 678", MeterPointDto::getNumber));
    }

    @Test
    void status404() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(new MeterPointDto().unitId(unit.getId()).number("12 345 678")),
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
      final MeterPoint meterPoint =
          meterPointTestDataService.newMeterPoint(
              m -> m.unit(unitTestDataService.newUnit()).number("12 635 851"));

      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              MeterPointDto.class,
              meterPoint.getId());

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }
}
