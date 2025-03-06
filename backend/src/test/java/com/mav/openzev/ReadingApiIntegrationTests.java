package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.mav.openzev.api.model.*;
import com.mav.openzev.entity.MeterPoint;
import com.mav.openzev.entity.Reading;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ReadingApiIntegrationTests extends AbstractApiIntegrationTest {

  private static final String V_1_READINGS = "/v1/readings";
  private static final String V_1_READINGS_ID = "/v1/readings/{readingId}";

  @Autowired private TestDataService testDataService;

  @Nested
  class GetReadingsTests {

    @Test
    void status200() {
      // arrange
      testDataService.newReading(
          v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("12 635 851"))));
      testDataService.newReading(
          v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("34 635 851"))));

      // act
      final ResponseEntity<ReadingDto[]> response =
          restTemplate.exchange(V_1_READINGS, HttpMethod.GET, HttpEntity.EMPTY, ReadingDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .hasSize(2)
                      .extracting(
                          ReadingDto::getDate, readingDto -> readingDto.getMeterPoint().getNumber())
                      .containsExactly(
                          tuple(LocalDate.of(2024, 1, 1), "12 635 851"),
                          tuple(LocalDate.of(2024, 1, 1), "34 635 851")));
    }
  }

  @Nested
  class CreateReadingTests {

    @Test
    void status204() {
      // arrange
      final MeterPoint meterPoint = testDataService.newMeterPoint(v -> v.setNumber("12 635 851"));

      final ModifiableReadingDto modifiableReadingDto =
          new ModifiableReadingDto()
              .meterPointId(meterPoint.getOid())
              .date(LocalDate.of(2025, 1, 1))
              .peakTariff(BigDecimal.valueOf(200))
              .offPeakTariff(BigDecimal.valueOf(100))
              .total(BigDecimal.valueOf(300));

      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS,
              HttpMethod.POST,
              new HttpEntity<>(modifiableReadingDto, null),
              ReadingDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("12 635 851", readingDto -> readingDto.getMeterPoint().getNumber())
                      .returns(LocalDate.of(2025, 1, 1), ReadingDto::getDate)
                      .returns(BigDecimal.valueOf(200), ReadingDto::getPeakTariff)
                      .returns(BigDecimal.valueOf(100), ReadingDto::getOffPeakTariff)
                      .returns(BigDecimal.valueOf(300), ReadingDto::getTotal));
    }

    @Test
    void status422_invalid_total() {
      // arrange
      final MeterPoint meterPoint = testDataService.newMeterPoint(v -> v.setNumber("12 635 851"));

      final ModifiableReadingDto modifiableReadingDto =
          new ModifiableReadingDto()
              .meterPointId(meterPoint.getOid())
              .date(LocalDate.of(2025, 1, 1))
              .peakTariff(BigDecimal.valueOf(200))
              .offPeakTariff(BigDecimal.valueOf(100))
              .total(BigDecimal.valueOf(500));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_READINGS,
              HttpMethod.POST,
              new HttpEntity<>(modifiableReadingDto, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("reading_total_incorrect", ErrorDto::getCode)
                      .satisfies(
                          e -> assertThat(e.getArgs()).containsExactly("200", "100", "500")));
    }

    @Test
    void status409_duplicated() {
      // arrange
      final Reading reading =
          testDataService.newReading(
              v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("12 635 851"))));

      final ModifiableReadingDto modifiableReadingDto =
          new ModifiableReadingDto()
              .meterPointId(reading.getMeterPoint().getOid())
              .date(reading.getDate())
              .peakTariff(BigDecimal.valueOf(200))
              .offPeakTariff(BigDecimal.valueOf(100))
              .total(BigDecimal.valueOf(300));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_READINGS,
              HttpMethod.POST,
              new HttpEntity<>(modifiableReadingDto, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CONFLICT, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns("reading_duplicated", ErrorDto::getCode));
    }
  }

  @Nested
  class GetReadingTests {

    @Test
    void status200() {
      // arrange
      final Reading reading =
          testDataService.newReading(
              v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("12 635 851"))));

      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ReadingDto.class,
              reading.getOid());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("12 635 851", readingDto -> readingDto.getMeterPoint().getNumber())
                      .returns(LocalDate.of(2024, 1, 1), ReadingDto::getDate)
                      .returns(BigDecimal.valueOf(100), ReadingDto::getPeakTariff)
                      .returns(BigDecimal.valueOf(50), ReadingDto::getOffPeakTariff)
                      .returns(BigDecimal.valueOf(150), ReadingDto::getTotal));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ReadingDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class ChangeReadingTests {

    @Test
    void status200() {
      // arrange
      final Reading reading =
          testDataService.newReading(
              v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("12 635 851"))));

      final ModifiableReadingDto modifiableReadingDto =
          new ModifiableReadingDto()
              .meterPointId(reading.getMeterPoint().getOid())
              .date(LocalDate.of(2025, 1, 1))
              .peakTariff(BigDecimal.valueOf(300))
              .offPeakTariff(BigDecimal.valueOf(150))
              .total(BigDecimal.valueOf(450));

      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(modifiableReadingDto),
              ReadingDto.class,
              reading.getOid());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("12 635 851", readingDto -> readingDto.getMeterPoint().getNumber())
                      .returns(LocalDate.of(2025, 1, 1), ReadingDto::getDate)
                      .returns(BigDecimal.valueOf(300), ReadingDto::getPeakTariff)
                      .returns(BigDecimal.valueOf(150), ReadingDto::getOffPeakTariff)
                      .returns(BigDecimal.valueOf(450), ReadingDto::getTotal));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(new ModifiableReadingDto()),
              ReadingDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status422_invalid_total() {
      // arrange
      final Reading reading =
          testDataService.newReading(
              v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("12 635 851"))));

      final ModifiableReadingDto modifiableReadingDto =
          new ModifiableReadingDto()
              .meterPointId(reading.getMeterPoint().getOid())
              .date(LocalDate.of(2025, 1, 1))
              .peakTariff(BigDecimal.valueOf(200))
              .offPeakTariff(BigDecimal.valueOf(100))
              .total(BigDecimal.valueOf(500));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(modifiableReadingDto, null),
              ErrorDto.class,
              reading.getOid());

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns("reading_total_incorrect", ErrorDto::getCode)
                      .satisfies(
                          e -> assertThat(e.getArgs()).containsExactly("200", "100", "500")));
    }

    @Test
    void status409_duplicated() {
      // arrange
      final Reading reading =
          testDataService.newReading(
              v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("12 635 851"))));

      final Reading reading2 =
          testDataService.newReading(
              v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("34 635 851"))));

      final ModifiableReadingDto modifiableReadingDto =
          new ModifiableReadingDto()
              .meterPointId(reading2.getMeterPoint().getOid())
              .date(reading2.getDate())
              .peakTariff(BigDecimal.valueOf(200))
              .offPeakTariff(BigDecimal.valueOf(100))
              .total(BigDecimal.valueOf(300));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(modifiableReadingDto, null),
              ErrorDto.class,
              reading.getOid());

      // assert
      assertThat(response)
          .returns(HttpStatus.CONFLICT, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns("reading_duplicated", ErrorDto::getCode));
    }
  }

  @Nested
  class DeleteReadingTests {

    @Test
    void status200() {
      // arrange
      final Reading reading =
          testDataService.newReading(
              v -> v.setMeterPoint(testDataService.newMeterPoint(m -> m.setNumber("12 635 851"))));

      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ReadingDto.class,
              reading.getOid());

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ReadingDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }
}
