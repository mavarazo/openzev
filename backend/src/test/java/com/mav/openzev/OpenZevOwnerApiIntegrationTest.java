package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableOwnerDto;
import com.mav.openzev.api.model.OwnerDto;
import com.mav.openzev.model.Owner;
import com.mav.openzev.repository.OwnerRepository;
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
public class OpenZevOwnerApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private OwnerRepository ownerRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetOwnersTests {

    @Test
    @Sql(scripts = {"/db/test-data/owners.sql"})
    void status200() {
      // act
      final ResponseEntity<OwnerDto[]> response =
          restTemplate.exchange(
              UriFactory.owners(), HttpMethod.GET, HttpEntity.EMPTY, OwnerDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetOwnerTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.owners("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("owner_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/owners.sql"})
    void status200() {
      // act
      final ResponseEntity<OwnerDto> response =
          restTemplate.exchange(
              UriFactory.owners("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              OwnerDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(
                          UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"), OwnerDto::getId)
                      .returns(true, OwnerDto::getActive)
                      .returns("6aeeaf0f-7e55-4bb8-8fb1-10f24fb8318c", OwnerDto::getContractId)
                      .returns("Anna", OwnerDto::getFirstName)
                      .returns("Barry", OwnerDto::getLastName)
                      .returns("anna@barry.com", OwnerDto::getEmail)
                      .returns("Stradun", OwnerDto::getStreet)
                      .returns("30", OwnerDto::getHouseNr)
                      .returns("1624", OwnerDto::getPostalCode)
                      .returns("Grattavache", OwnerDto::getCity)
                      .returns("+41 41 555 66 77", OwnerDto::getPhoneNr)
                      .returns("+41 79 555 66 77", OwnerDto::getMobileNr));
    }
  }

  @Nested
  class CreateOwnerTests {

    @ParameterizedTest
    @CsvSource(value = {"Anna,", ",Barry"})
    void status400(final String firstName, final String lastName) {
      // arrange
      final ModifiableOwnerDto requestBody =
          new ModifiableOwnerDto().firstName(firstName).lastName(lastName);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.owners(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status201() {
      // arrange
      final ModifiableOwnerDto requestBody =
          new ModifiableOwnerDto()
              .contractId("7f382429-6fc5-424b-84b6-3e6177db63d4")
              .firstName("Anna")
              .lastName("Barry")
              .email("anna@barry.com")
              .street("Stradun")
              .houseNr("30")
              .postalCode("1624")
              .city("Grattavache")
              .phoneNr("+41 41 555 66 77")
              .mobileNr("+41 79 555 66 77");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.owners(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(ownerRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              owner ->
                  assertThat(owner)
                      .returns(true, Owner::isActive)
                      .returns("7f382429-6fc5-424b-84b6-3e6177db63d4", Owner::getContractId)
                      .returns("Anna", Owner::getFirstName)
                      .returns("Barry", Owner::getLastName)
                      .returns("anna@barry.com", Owner::getEmail)
                      .returns("Stradun", Owner::getStreet)
                      .returns("30", Owner::getHouseNr)
                      .returns("1624", Owner::getPostalCode)
                      .returns("Grattavache", Owner::getCity)
                      .returns("+41 41 555 66 77", Owner::getPhoneNr)
                      .returns("+41 79 555 66 77", Owner::getMobileNr));
    }
  }

  @Nested
  class ChangeOwnerTests {

    @ParameterizedTest
    @CsvSource(
        value = {
          ",Barry,Stradun,30,1624,Grattavache",
          "Anna,,Stradun,30,1624,Grattavache",
          "Anna,Barry,,30,1624,Grattavache",
          "Anna,Barry,Stradun,,1624,Grattavache",
          "Anna,Barry,Stradun,30,,Grattavache",
          "Anna,Barry,Stradun,30,1624,"
        })
    @Sql(scripts = {"/db/test-data/owners.sql"})
    void status400(
        final String firstName,
        final String lastName,
        final String street,
        final String houseNr,
        final String postalCode,
        final String city) {
      // arrange
      final ModifiableOwnerDto requestBody =
          new ModifiableOwnerDto()
              .firstName(firstName)
              .lastName(lastName)
              .street(street)
              .houseNr(houseNr)
              .postalCode(postalCode)
              .city(city);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.owners("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiableOwnerDto requestBody =
          new ModifiableOwnerDto()
              .contractId("7f382429-6fc5-424b-84b6-3e6177db63d5")
              .firstName("Jordan")
              .lastName("Sheppard")
              .email("jordan@sheppard.com")
              .street("Casa Posrclas")
              .houseNr("57")
              .postalCode("6013")
              .city("Eigenthal")
              .phoneNr("+41 41 555 66 77")
              .mobileNr("+41 79 555 66 77");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.owners("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("owner_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/owners.sql"})
    void status200() {
      // arrange
      final ModifiableOwnerDto requestBody =
          new ModifiableOwnerDto()
              .contractId("7f382429-6fc5-424b-84b6-3e6177db63d5")
              .firstName("Jordan")
              .lastName("Sheppard")
              .email("jordan@sheppard.com")
              .street("Casa Posrclas")
              .houseNr("57")
              .postalCode("6013")
              .city("Eigenthal")
              .phoneNr("+41 41 555 66 77")
              .mobileNr("+41 79 555 66 77");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.owners("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(ownerRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              owner ->
                  assertThat(owner)
                      .returns(
                          UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"), Owner::getUuid)
                      .returns(true, Owner::isActive)
                      .returns("7f382429-6fc5-424b-84b6-3e6177db63d5", Owner::getContractId)
                      .returns("Jordan", Owner::getFirstName)
                      .returns("Sheppard", Owner::getLastName)
                      .returns("jordan@sheppard.com", Owner::getEmail)
                      .returns("Casa Posrclas", Owner::getStreet)
                      .returns("57", Owner::getHouseNr)
                      .returns("6013", Owner::getPostalCode)
                      .returns("Eigenthal", Owner::getCity)
                      .returns("+41 41 555 66 77", Owner::getPhoneNr)
                      .returns("+41 79 555 66 77", Owner::getMobileNr));
    }
  }

  @Nested
  class DeleteOwnerTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.owners("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("owner_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/owners.sql",
          "/db/test-data/units.sql",
          "/db/test-data/ownerships.sql"
        })
    void status422() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.owners("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("owner_has_ownership", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/owners.sql"})
    void status204() {
      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.owners("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
