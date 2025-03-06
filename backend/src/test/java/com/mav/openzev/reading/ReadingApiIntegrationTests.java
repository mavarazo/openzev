package com.mav.openzev.reading;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.AbstractApiIntegrationTest;
import com.mav.openzev.api.model.*;
import com.mav.openzev.reading.entity.Reading;
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

class ReadingApiIntegrationTests extends AbstractApiIntegrationTest {

  private static final String V_1_READINGS = "/v1/readings";
  private static final String V_1_READINGS_ID = "/v1/readings/{readingId}";

  @Autowired private ReadingTestDataService readingTestDataService;

  @Nested
  class GetReadingsTests {

    @Test
    void status200() {
      // arrange
      IntStream.range(2024, 2030)
          .forEach(
              i -> {
                readingTestDataService.newReading(r -> r.date(LocalDate.of(i, 1, 1)));
              });

      // act
      final ResponseEntity<ReadingDto[]> response =
          restTemplate.exchange(V_1_READINGS, HttpMethod.GET, HttpEntity.EMPTY, ReadingDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(6));
    }
  }

  @Nested
  class CreateReadingTests {

    @Test
    void status201() {
      // arrange
      final ReadingDto readingDto = new ReadingDto().date(LocalDate.of(2025, 1, 1));

      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS, HttpMethod.POST, new HttpEntity<>(readingDto, null), ReadingDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .satisfies(
              r -> assertThat(r.getBody()).returns(LocalDate.of(2025, 1, 1), ReadingDto::getDate));
    }

    @Test
    void status409_duplicated() {
      // arrange
      final Reading reading = readingTestDataService.newReading();
      final ReadingDto readingDto = new ReadingDto().date(reading.getDate());

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_READINGS, HttpMethod.POST, new HttpEntity<>(readingDto, null), ErrorDto.class);

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
      final Reading reading = readingTestDataService.newReading();

      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID, HttpMethod.GET, HttpEntity.EMPTY, ReadingDto.class, reading.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r -> assertThat(r.getBody()).returns(LocalDate.of(2024, 1, 1), ReadingDto::getDate));
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
      final Reading reading = readingTestDataService.newReading();
      final ReadingDto readingDto = new ReadingDto().date(LocalDate.of(2025, 1, 1));

      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(readingDto),
              ReadingDto.class,
              reading.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r -> assertThat(r.getBody()).returns(LocalDate.of(2025, 1, 1), ReadingDto::getDate));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(new ReadingDto().date(LocalDate.now())),
              ReadingDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status409_duplicated() {
      // arrange
      final Reading reading = readingTestDataService.newReading();
      final Reading reading2 =
          readingTestDataService.newReading(r -> r.date(LocalDate.of(2025, 1, 1)));

      final ReadingDto readingDto = new ReadingDto().date(reading2.getDate());

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(readingDto, null),
              ErrorDto.class,
              reading.getId());

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
      final Reading reading = readingTestDataService.newReading();

      // act
      final ResponseEntity<ReadingDto> response =
          restTemplate.exchange(
              V_1_READINGS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ReadingDto.class,
              reading.getId());

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
