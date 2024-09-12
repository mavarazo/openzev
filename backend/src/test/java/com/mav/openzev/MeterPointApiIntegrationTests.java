package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.MeterPointDto;
import com.mav.openzev.api.model.ModifiableMeterPointDto;
import com.mav.openzev.entity.MeterPoint;
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

  @Autowired private TestDataService testDataService;

  @Nested
  class GetMeterPointsTests {

    @Test
    void status200() {
      // arrange
      testDataService.newMeterPoint(v -> v.setNumber("12 635 851"));
      testDataService.newMeterPoint(v -> v.setNumber("34 635 851"));

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
      final ModifiableMeterPointDto modifiableMeterPointDto =
          new ModifiableMeterPointDto().number("12 635 851");

      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS,
              HttpMethod.POST,
              new HttpEntity<>(modifiableMeterPointDto, null),
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
      final MeterPoint meterPoint = testDataService.newMeterPoint(v -> v.setNumber("12 635 851"));

      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              MeterPointDto.class,
              meterPoint.getOid());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns("12 635 851", MeterPointDto::getNumber));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              MeterPointDto.class,
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
      final MeterPoint meterPoint = testDataService.newMeterPoint(v -> v.setNumber("12 635 851"));

      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(new ModifiableMeterPointDto().number("12 345 678")),
              MeterPointDto.class,
              meterPoint.getOid());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns("12 345 678", MeterPointDto::getNumber));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<MeterPointDto> response =
          restTemplate.exchange(
              V_1_METER_POINTS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(new ModifiableMeterPointDto().number("12 345 678")),
              MeterPointDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }
}
