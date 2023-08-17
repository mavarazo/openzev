package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableUserDto;
import com.mav.openzev.api.model.UserDto;
import com.mav.openzev.model.User;
import com.mav.openzev.repository.UserRepository;
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
public class OpenZevUserApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private UserRepository userRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetUsersTests {

    @Test
    @Sql(scripts = {"/db/test-data/users.sql"})
    void status200() {
      // act
      final ResponseEntity<UserDto[]> response =
          restTemplate.exchange(
              UriFactory.users(), HttpMethod.GET, HttpEntity.EMPTY, UserDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetUserTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.users("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/users.sql"})
    void status200() {
      // act
      final ResponseEntity<UserDto> response =
          restTemplate.exchange(
              UriFactory.users("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              UserDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(
                          UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"), UserDto::getUuid)
                      .returns(true, UserDto::getActive)
                      .returns("6aeeaf0f-7e55-4bb8-8fb1-10f24fb8318c", UserDto::getContractId)
                      .returns("Anna", UserDto::getFirstName)
                      .returns("Barry", UserDto::getLastName)
                      .returns("anna@barry.com", UserDto::getEmail)
                      .returns("Stradun", UserDto::getStreet)
                      .returns("30", UserDto::getHouseNr)
                      .returns("1624", UserDto::getPostalCode)
                      .returns("Grattavache", UserDto::getCity)
                      .returns("+41 41 555 66 77", UserDto::getPhoneNr)
                      .returns("+41 79 555 66 77", UserDto::getMobileNr));
    }
  }

  @Nested
  class CreateUserTests {

    @ParameterizedTest
    @CsvSource(value = {"Anna,", ",Barry"})
    void status400(final String firstName, final String lastName) {
      // arrange
      final ModifiableUserDto requestBody =
          new ModifiableUserDto().firstName(firstName).lastName(lastName);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.users(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status201() {
      // arrange
      final ModifiableUserDto requestBody =
          new ModifiableUserDto()
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
              UriFactory.users(), HttpMethod.POST, new HttpEntity<>(requestBody, null), UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(userRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              user ->
                  assertThat(user)
                      .returns(true, User::isActive)
                      .returns("7f382429-6fc5-424b-84b6-3e6177db63d4", User::getContractId)
                      .returns("Anna", User::getFirstName)
                      .returns("Barry", User::getLastName)
                      .returns("anna@barry.com", User::getEmail)
                      .returns("Stradun", User::getStreet)
                      .returns("30", User::getHouseNr)
                      .returns("1624", User::getPostalCode)
                      .returns("Grattavache", User::getCity)
                      .returns("+41 41 555 66 77", User::getPhoneNr)
                      .returns("+41 79 555 66 77", User::getMobileNr));
    }
  }

  @Nested
  class ChangeUserTests {

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
    @Sql(scripts = {"/db/test-data/users.sql"})
    void status400(
        final String firstName,
        final String lastName,
        final String street,
        final String houseNr,
        final String postalCode,
        final String city) {
      // arrange
      final ModifiableUserDto requestBody =
          new ModifiableUserDto()
              .firstName(firstName)
              .lastName(lastName)
              .street(street)
              .houseNr(houseNr)
              .postalCode(postalCode)
              .city(city);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.users("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiableUserDto requestBody =
          new ModifiableUserDto()
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
              UriFactory.users("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/users.sql"})
    void status200() {
      // arrange
      final ModifiableUserDto requestBody =
          new ModifiableUserDto()
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
              UriFactory.users("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(userRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              user ->
                  assertThat(user)
                      .returns(
                          UUID.fromString("790772bd-6425-41af-9270-297eb0d42060"), User::getUuid)
                      .returns(true, User::isActive)
                      .returns("7f382429-6fc5-424b-84b6-3e6177db63d5", User::getContractId)
                      .returns("Jordan", User::getFirstName)
                      .returns("Sheppard", User::getLastName)
                      .returns("jordan@sheppard.com", User::getEmail)
                      .returns("Casa Posrclas", User::getStreet)
                      .returns("57", User::getHouseNr)
                      .returns("6013", User::getPostalCode)
                      .returns("Eigenthal", User::getCity)
                      .returns("+41 41 555 66 77", User::getPhoneNr)
                      .returns("+41 79 555 66 77", User::getMobileNr));
    }
  }

  @Nested
  class DeleteUserTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.users("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/users.sql",
          "/db/test-data/units.sql",
          "/db/test-data/ownerships.sql"
        })
    void status422() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.users("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_has_ownership", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/users.sql"})
    void status204() {
      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.users("790772bd-6425-41af-9270-297eb0d42060"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
