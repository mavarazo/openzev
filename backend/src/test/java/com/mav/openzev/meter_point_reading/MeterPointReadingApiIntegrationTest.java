package com.mav.openzev.meter_point_reading;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.AbstractApiIntegrationTest;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.MeterPointReadingDto;
import com.mav.openzev.meter_point.MeterPointTestDataService;
import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import com.mav.openzev.reading.ReadingTestDataService;
import com.mav.openzev.reading.entity.Reading;
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
import org.springframework.web.util.UriComponentsBuilder;

class MeterPointReadingApiIntegrationTest extends AbstractApiIntegrationTest {

  private static final String V_1_METER_POINT_READINGS = "/v1/meter-point-readings";

  private static final String V_1_METER_POINT_READINGS_ID =
      "/v1/meter-point-readings/{meterPointReadingId}";

  @Autowired private MeterPointTestDataService meterPointTestDataService;
  @Autowired private MeterPointReadingTestDataService meterPointReadingTestDataService;
  @Autowired private ReadingTestDataService readingTestDataService;
  @Autowired private UnitTestDataService unitTestDataService;

  @Nested
  class GetMeterPointReadingsTests {

    @Test
    void status200() {
      // arrange
      IntStream.range(2024, 2030)
          .forEach(
              i -> {
                final Unit unit = unitTestDataService.newUnit();
                final MeterPoint meterPoint =
                    meterPointTestDataService.newMeterPoint(
                        m -> m.unit(unit).number(String.valueOf(i)));

                final Reading reading =
                    readingTestDataService.newReading(r -> r.date(LocalDate.of(i, 1, 1)));

                meterPointReadingTestDataService.newMeterPointReading(
                    m -> m.reading(reading).meterPoint(meterPoint));
              });

      // act
      final ResponseEntity<MeterPointReadingDto[]> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              MeterPointReadingDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(6));
    }

    @Test
    void status200_query_reading() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));

      final Reading reading = readingTestDataService.newReading();
      meterPointReadingTestDataService.newMeterPointReading(
          m -> m.meterPoint(meterPoint).reading(reading));

      // act
      final ResponseEntity<MeterPointReadingDto[]> response =
          restTemplate.exchange(
              UriComponentsBuilder.fromUriString(V_1_METER_POINT_READINGS)
                  .queryParam("readingId", reading.getId())
                  .build()
                  .toUriString(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              MeterPointReadingDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class CreateMeterPointReadingTests {

    @Test
    void status201() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Reading reading = readingTestDataService.newReading();

      final MeterPointReadingDto meterPointReadingDto =
          new MeterPointReadingDto()
              .readingId(reading.getId())
              .meterPointId(meterPoint.getId())
              .peakTariff(BigDecimal.TWO)
              .offPeakTariff(BigDecimal.ONE)
              .total(BigDecimal.valueOf(3));

      // act
      final ResponseEntity<MeterPointReadingDto> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS,
              HttpMethod.POST,
              new HttpEntity<>(meterPointReadingDto, null),
              MeterPointReadingDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(reading.getId(), m -> m.getReading().getId())
                      .returns(meterPoint.getId(), m -> m.getMeterPoint().getId())
                      .satisfies(
                          m -> assertThat(m.getPeakTariff()).isEqualByComparingTo(BigDecimal.TWO))
                      .satisfies(
                          m ->
                              assertThat(m.getOffPeakTariff()).isEqualByComparingTo(BigDecimal.ONE))
                      .satisfies(
                          m ->
                              assertThat(m.getTotal())
                                  .isEqualByComparingTo(BigDecimal.valueOf(3))));
    }

    @Test
    void status400() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Reading reading = readingTestDataService.newReading();

      final MeterPointReadingDto meterPointReadingDto =
          new MeterPointReadingDto()
              .readingId(reading.getId())
              .meterPointId(meterPoint.getId())
              .peakTariff(BigDecimal.TWO)
              .offPeakTariff(BigDecimal.ONE)
              .total(BigDecimal.TEN);

      // act
      final ResponseEntity<ErrorDto[]> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS,
              HttpMethod.POST,
              new HttpEntity<>(meterPointReadingDto, null),
              ErrorDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .singleElement()
                      .returns("total_invalid", ErrorDto::getCode));
    }

    @Test
    void status404_reading_not_found() {
      // arrange
      final MeterPointReadingDto meterPointReadingDto =
          new MeterPointReadingDto()
              .readingId(UUID.randomUUID())
              .meterPointId(UUID.randomUUID())
              .peakTariff(BigDecimal.TWO)
              .offPeakTariff(BigDecimal.ONE)
              .total(BigDecimal.valueOf(3));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS,
              HttpMethod.POST,
              new HttpEntity<>(meterPointReadingDto, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns("reading_not_found", ErrorDto::getCode));
    }

    @Test
    void status404_meter_point_not_found() {
      // arrange
      final Reading reading = readingTestDataService.newReading();

      final MeterPointReadingDto meterPointReadingDto =
          new MeterPointReadingDto()
              .readingId(reading.getId())
              .meterPointId(UUID.randomUUID())
              .peakTariff(BigDecimal.TWO)
              .offPeakTariff(BigDecimal.ONE)
              .total(BigDecimal.valueOf(3));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS,
              HttpMethod.POST,
              new HttpEntity<>(meterPointReadingDto, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .satisfies(
              r -> assertThat(r.getBody()).returns("meterpoint_not_found", ErrorDto::getCode));
    }
  }

  @Nested
  class GetMeterPointReadingTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Reading reading = readingTestDataService.newReading();
      final MeterPointReading meterPointReading =
          meterPointReadingTestDataService.newMeterPointReading(
              m -> m.reading(reading).meterPoint(meterPoint));

      // act
      final ResponseEntity<MeterPointReadingDto> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              MeterPointReadingDto.class,
              meterPointReading.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(reading.getId(), m -> m.getReading().getId())
                      .returns(meterPoint.getId(), m -> m.getMeterPoint().getId())
                      .satisfies(
                          m -> assertThat(m.getPeakTariff()).isEqualByComparingTo(BigDecimal.TWO))
                      .satisfies(
                          m ->
                              assertThat(m.getOffPeakTariff()).isEqualByComparingTo(BigDecimal.ONE))
                      .satisfies(
                          m ->
                              assertThat(m.getTotal())
                                  .isEqualByComparingTo(BigDecimal.valueOf(3))));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("meterpointreading_not_found", ErrorDto::getCode));
    }
  }

  @Nested
  class ChangeMeterPointReadingTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Reading reading = readingTestDataService.newReading();
      final MeterPointReading meterPointReading =
          meterPointReadingTestDataService.newMeterPointReading(
              m -> m.reading(reading).meterPoint(meterPoint));

      final MeterPointReadingDto meterPointReadingDto =
          new MeterPointReadingDto()
              .readingId(reading.getId())
              .meterPointId(meterPoint.getId())
              .peakTariff(BigDecimal.TWO)
              .offPeakTariff(BigDecimal.ONE)
              .total(BigDecimal.valueOf(3));

      // act
      final ResponseEntity<MeterPointReadingDto> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(meterPointReadingDto, null),
              MeterPointReadingDto.class,
              meterPointReading.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(reading.getId(), m -> m.getReading().getId())
                      .returns(meterPoint.getId(), m -> m.getMeterPoint().getId())
                      .satisfies(
                          m -> assertThat(m.getPeakTariff()).isEqualByComparingTo(BigDecimal.TWO))
                      .satisfies(
                          m ->
                              assertThat(m.getOffPeakTariff()).isEqualByComparingTo(BigDecimal.ONE))
                      .satisfies(
                          m ->
                              assertThat(m.getTotal())
                                  .isEqualByComparingTo(BigDecimal.valueOf(3))));
    }

    @Test
    void status400() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Reading reading = readingTestDataService.newReading();
      final MeterPointReading meterPointReading =
          meterPointReadingTestDataService.newMeterPointReading(
              m -> m.reading(reading).meterPoint(meterPoint));

      final MeterPointReadingDto meterPointReadingDto =
          new MeterPointReadingDto()
              .readingId(reading.getId())
              .meterPointId(meterPoint.getId())
              .peakTariff(BigDecimal.TWO)
              .offPeakTariff(BigDecimal.ONE)
              .total(BigDecimal.TEN);

      // act
      final ResponseEntity<ErrorDto[]> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(meterPointReadingDto, null),
              ErrorDto[].class,
              meterPointReading.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .singleElement()
                      .returns("total_invalid", ErrorDto::getCode));
    }

    @Test
    void status404() {
      // arrange
      final MeterPointReadingDto meterPointReadingDto =
          new MeterPointReadingDto()
              .readingId(UUID.randomUUID())
              .meterPointId(UUID.randomUUID())
              .peakTariff(BigDecimal.TWO)
              .offPeakTariff(BigDecimal.ONE)
              .total(BigDecimal.valueOf(3));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(meterPointReadingDto, null),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("meterpointreading_not_found", ErrorDto::getCode));
    }
  }

  @Nested
  class DeleteMeterPointReadingTests {

    @Test
    void status204() {
      // arrange
      final Unit unit = unitTestDataService.newUnit();
      final MeterPoint meterPoint = meterPointTestDataService.newMeterPoint(m -> m.unit(unit));
      final Reading reading = readingTestDataService.newReading();
      final MeterPointReading meterPointReading =
          meterPointReadingTestDataService.newMeterPointReading(
              m -> m.reading(reading).meterPoint(meterPoint));

      // act
      final ResponseEntity<Void> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              Void.class,
              meterPointReading.getId());

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_METER_POINT_READINGS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("meterpointreading_not_found", ErrorDto::getCode));
    }
  }
}
