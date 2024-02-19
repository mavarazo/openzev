package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableOwnershipDto;
import com.mav.openzev.api.model.OwnershipDto;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.OwnerModels;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.model.OwnershipModels;
import com.mav.openzev.model.Unit;
import com.mav.openzev.model.UnitModels;
import com.mav.openzev.repository.OwnershipRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

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
    void status200() {
      // arrange
      final Owner owner = testDatabaseService.insert(OwnerModels.getOwner());
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      testDatabaseService.insert(OwnershipModels.getOwnership(owner, unit));

      // act
      final ResponseEntity<OwnershipDto[]> response =
          restTemplate.exchange(
              UriFactory.units_ownerships(UnitModels.UUID),
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
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.ownerships(OwnershipModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("ownership_not_found", ErrorDto::getCode)
          .returns(
              "ownership with id '" + OwnershipModels.UUID + "' not found", ErrorDto::getMessage);
    }

    @Test
    void status200() {
      // arrange
      final Owner owner = testDatabaseService.insert(OwnerModels.getOwner());
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      testDatabaseService.insert(OwnershipModels.getOwnership(owner, unit));

      // act
      final ResponseEntity<OwnershipDto> response =
          restTemplate.exchange(
              UriFactory.ownerships(OwnershipModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              OwnershipDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r -> assertThat(r.getBody()).returns(OwnershipModels.UUID, OwnershipDto::getId));
    }
  }

  @Nested
  class CreateOwnershipTests {

    @ParameterizedTest
    @RequiredSource(value = ModifiableOwnershipDto.class)
    void status400(final ModifiableOwnershipDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.units_ownerships(UnitModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status201() {
      // arrange
      testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(UnitModels.getUnit());

      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .ownerId(OwnerModels.UUID)
              .periodFrom(LocalDate.of(2020, 1, 1));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.units_ownerships(UnitModels.UUID),
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
                          o -> o.getOwner().getUuid()));
    }
  }

  @Nested
  class ChangeOwnershipTests {

    @ParameterizedTest
    @RequiredSource(value = ModifiableOwnershipDto.class)
    void status400(final ModifiableOwnershipDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.ownerships(UnitModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .ownerId(OwnerModels.UUID)
              .periodFrom(LocalDate.of(2020, 1, 1));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.ownerships(OwnershipModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("ownership_not_found", ErrorDto::getCode)
          .returns(
              "ownership with id '" + OwnershipModels.UUID + "' not found", ErrorDto::getMessage);
    }

    @Test
    void status200() {
      // arrange
      final Owner owner = testDatabaseService.insert(OwnerModels.getOwner());
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      testDatabaseService.insert(OwnershipModels.getOwnership(owner, unit));

      final ModifiableOwnershipDto requestBody =
          new ModifiableOwnershipDto()
              .active(false)
              .ownerId(OwnerModels.UUID)
              .periodFrom(LocalDate.of(2019, 1, 1));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.ownerships(OwnershipModels.UUID),
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
              ownership ->
                  assertThat(ownership)
                      .returns(UnitModels.UUID, o -> o.getUnit().getUuid())
                      .returns(OwnerModels.UUID, o -> o.getOwner().getUuid())
                      .returns(false, Ownership::isActive)
                      .returns(LocalDate.of(2019, 1, 1), Ownership::getPeriodFrom));
    }
  }

  @Nested
  class DeleteOwnershipTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.ownerships(OwnershipModels.UUID),
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("ownership_not_found", ErrorDto::getCode);
    }

    @Test
    void status204() {
      // arrange
      final Owner owner = testDatabaseService.insert(OwnerModels.getOwner());
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      testDatabaseService.insert(OwnershipModels.getOwnership(owner, unit));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.ownerships(OwnershipModels.UUID),
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
