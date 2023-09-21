package com.mav.openzev;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableOwnershipDto;
import com.mav.openzev.api.model.OwnershipDto;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.repository.OwnershipRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
public class OpenZevOwnershipApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private OwnershipRepository ownershipRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetOwnershipsTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units_ownerships("414d2033-3b17-4e68-b69e-e483db0dc90b"),
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
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    void status200() {
      // act
      final ResponseEntity<OwnershipDto[]> response =
          restTemplate.exchange(
              UriFactory.units_ownerships("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              OwnershipDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(2));
    }

    @ParameterizedTest
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    @CsvSource(value = {"2022-01-01,", ",2023-12-31"})
    void status200_filtered_by_either_validity(
        final LocalDate validFrom, final LocalDate validUpto) {
      // act
      final ResponseEntity<OwnershipDto[]> response =
          restTemplate.exchange(
              UriFactory.units_ownerships(
                  "414d2033-3b17-4e68-b69e-e483db0dc90b", validFrom, validUpto),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              OwnershipDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(2));
    }

    @ParameterizedTest
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    @CsvSource(value = {"2022-01-01,2022-12-31"})
    void status200_filtered_by_both_validity(final LocalDate validFrom, final LocalDate validUpto) {
      // act
      final ResponseEntity<OwnershipDto[]> response =
          restTemplate.exchange(
              UriFactory.units_ownerships(
                  "414d2033-3b17-4e68-b69e-e483db0dc90b", validFrom, validUpto),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              OwnershipDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetOwnershipTests {

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
        })
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.ownerships("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("ownership_not_found", ErrorDto::getCode)
          .returns(
              "ownership with id '414d2033-3b17-4e68-b69e-e483db0dc90b' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    void status200() {
      // act
      final ResponseEntity<OwnershipDto> response =
          restTemplate.exchange(
              UriFactory.ownerships("bbfe1426-3a77-40da-9947-f970adce3735"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              OwnershipDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(
                          UUID.fromString("bbfe1426-3a77-40da-9947-f970adce3735"),
                          OwnershipDto::getUuid));
    }
  }

  @Nested
  class CreateOwnershipTests {

    @ParameterizedTest
    @CsvSource({", 2023-01-01", "790772bd-6425-41af-9270-297eb0d42060,"})
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    void status400(final String userId, final LocalDate periodFrom) {
      // arrange
      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .user(nonNull(userId) ? UUID.fromString(userId) : null)
              .periodFrom(periodFrom);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units_ownerships("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    void status422() {
      // arrange
      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .user(UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"))
              .periodFrom(LocalDate.of(2023, 1, 1));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units_ownerships("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(HttpEntity::getBody)
          .returns("ownership_overlap", ErrorDto::getCode)
          .returns("ownership '2023-01-01 - ' overlaps with '2023-01-01 - '", ErrorDto::getMessage);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
        })
    void status201() {
      // arrange
      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .user(UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"))
              .periodFrom(LocalDate.of(2023, 1, 1));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.units_ownerships("414d2033-3b17-4e68-b69e-e483db0dc90b"),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(ownershipRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              ownership ->
                  assertThat(ownership)
                      .returns(
                          UUID.fromString("414d2033-3b17-4e68-b69e-e483db0dc90b"),
                          o -> o.getUnit().getUuid())
                      .returns(
                          UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"),
                          o -> o.getUser().getUuid())
                      .returns(LocalDate.of(2023, 1, 1), Ownership::getPeriodFrom)
                      .returns(null, Ownership::getPeriodUpto));
    }
  }

  @Nested
  class ChangeOwnershipTests {

    @ParameterizedTest
    @CsvSource({", 2023-01-01", "790772bd-6425-41af-9270-297eb0d42060,"})
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    void status400(final String userId, final LocalDate periodFrom) {
      // arrange
      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .user(nonNull(userId) ? UUID.fromString(userId) : null)
              .periodFrom(periodFrom);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.ownerships("bbfe1426-3a77-40da-9947-f970adce3735"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
        })
    void status404() {
      // arrange
      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .user(UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"))
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.ownerships("bbfe1426-3a77-40da-9947-f970adce3735"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("ownership_not_found", ErrorDto::getCode)
          .returns(
              "ownership with id 'bbfe1426-3a77-40da-9947-f970adce3735' not found",
              ErrorDto::getMessage);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    void status200() {
      // arrange
      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .user(UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"))
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.ownerships("bbfe1426-3a77-40da-9947-f970adce3735"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(ownershipRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              unit ->
                  assertThat(unit)
                      .returns(
                          UUID.fromString("bbfe1426-3a77-40da-9947-f970adce3735"),
                          Ownership::getUuid)
                      .returns(
                          UUID.fromString("414d2033-3b17-4e68-b69e-e483db0dc90b"),
                          o -> o.getUnit().getUuid())
                      .returns(
                          UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"),
                          o -> o.getUser().getUuid())
                      .returns(LocalDate.of(2023, 1, 1), Ownership::getPeriodFrom)
                      .returns(LocalDate.of(2023, 12, 31), Ownership::getPeriodUpto));
    }
  }

  @Nested
  class DeleteOwnershipTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.ownerships("bbfe1426-3a77-40da-9947-f970adce3735"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("ownership_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/units.sql",
          "/db/test-data/users.sql",
          "/db/test-data/ownerships.sql"
        })
    void status204() {
      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.ownerships("bbfe1426-3a77-40da-9947-f970adce3735"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
