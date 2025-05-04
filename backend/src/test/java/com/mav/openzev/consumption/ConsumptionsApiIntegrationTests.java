package com.mav.openzev.consumption;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.AbstractApiIntegrationTest;
import com.mav.openzev.api.model.*;
import com.mav.openzev.consumption.entity.Consumption;
import com.mav.openzev.meter_point.MeterPointTestDataService;
import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.unit.UnitTestDataService;
import com.mav.openzev.unit.entity.Unit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ConsumptionsApiIntegrationTests extends AbstractApiIntegrationTest {

  private static final String V_1_CONSUMPTIONS = "/v1/consumptions";
  private static final String V_1_CONSUMPTIONS_ID = "/v1/consumptions/{consumptionId}";

  @Autowired private ConsumptionTestDataService consumptionTestDataService;
  @Autowired private MeterPointTestDataService meterPointTestDataService;
  @Autowired private UnitTestDataService unitTestDataService;

  @Nested
  class GetConsumptionsTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      IntStream.range(2024, 2030)
          .forEach(
              i -> {
                consumptionTestDataService.newConsumption(
                    c -> c.meterPoint(meterPoint).date(LocalDate.of(i, 1, 1)));
              });

      // act
      final ResponseEntity<ConsumptionDto[]> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS, HttpMethod.GET, HttpEntity.EMPTY, ConsumptionDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(6));
    }

    @Test
    void status200_query_by_meter_point_id() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      IntStream.range(2024, 2030)
          .forEach(
              i -> {
                consumptionTestDataService.newConsumption(
                    c -> c.meterPoint(meterPoint).date(LocalDate.of(i, 1, 1)));
              });

      // act
      final ResponseEntity<ConsumptionDto[]> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS + "?meterPointId=" + meterPoint.getId(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ConsumptionDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(6));
    }
  }

  @Nested
  class CreateConsumptionTests {

    @Test
    void status201() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final ConsumptionDto consumptionDto =
          new ConsumptionDto()
              .meterPointId(meterPoint.getId())
              .date(LocalDate.of(2025, 1, 1))
              .total(BigDecimal.ONE);

      // act
      final ResponseEntity<ConsumptionDto> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS,
              HttpMethod.POST,
              new HttpEntity<>(consumptionDto, null),
              ConsumptionDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(LocalDate.of(2025, 1, 1), ConsumptionDto::getDate));
    }
  }

  @Nested
  class GetConsumptionTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Consumption consumption =
          consumptionTestDataService.newConsumption(c -> c.meterPoint(meterPoint));

      // act
      final ResponseEntity<ConsumptionDto> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ConsumptionDto.class,
              consumption.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(LocalDate.of(2024, 1, 1), ConsumptionDto::getDate));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class ChangeConsumptionTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Consumption consumption =
          consumptionTestDataService.newConsumption(c -> c.meterPoint(meterPoint));
      final ConsumptionDto consumptionDto =
          new ConsumptionDto()
              .meterPointId(meterPoint.getId())
              .date(LocalDate.of(2025, 1, 1))
              .total(BigDecimal.TEN);

      // act
      final ResponseEntity<ConsumptionDto> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(consumptionDto),
              ConsumptionDto.class,
              consumption.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(LocalDate.of(2025, 1, 1), ConsumptionDto::getDate));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ConsumptionDto> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(
                  new ConsumptionDto()
                      .meterPointId(UUID.randomUUID())
                      .date(LocalDate.now())
                      .total(BigDecimal.TEN)),
              ConsumptionDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class DeleteConsumptionTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Consumption consumption =
          consumptionTestDataService.newConsumption(c -> c.meterPoint(meterPoint));

      // act
      final ResponseEntity<Void> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              Void.class,
              consumption.getId());

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ConsumptionDto> response =
          restTemplate.exchange(
              V_1_CONSUMPTIONS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ConsumptionDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }
}
