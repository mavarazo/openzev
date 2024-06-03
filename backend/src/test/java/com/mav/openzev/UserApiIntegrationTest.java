package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.*;
import com.mav.openzev.api.model.UserRole;
import com.mav.openzev.model.*;
import com.mav.openzev.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserApiIntegrationTest extends AbstractApiIntegrationTest {

  private static final String V1_USERS = "/v1/users";
  private static final String V1_USERS_ID = "/v1/users/{userId}";
  private static final String V1_USERS_ID_PASSWORD = "/v1/users/{userId}/password";
  private static final String V1_SETTINGS_USERS = "/v1/settings/users";
  private static final String V1_SETTINGS_USERS_ID_PASSWORD =
      "/v1/settings/users/{userId}/password";

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Nested
  class SettingsTests {
    @Nested
    class GetUsersTests {

      @Test
      void status200() {
        // act
        final ResponseEntity<UserDto[]> response =
            restTemplate.exchange(
                V1_SETTINGS_USERS,
                HttpMethod.GET,
                new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
                UserDto[].class);

        // assert
        assertThat(response)
            .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
            .satisfies(r -> assertThat(r.getBody()).hasSize(1));
      }
    }

    @Nested
    class InviteUserTests {

      @Test
      void status201() {
        // arrange
        final ModifiableInvitationDto requestBody =
            new ModifiableInvitationDto()
                .email("esther@chavez.com")
                .password("{noop}password")
                .role(UserRole.SUPERUSER);

        // act
        final ResponseEntity<UUID> response =
            restTemplate.exchange(
                V1_SETTINGS_USERS,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
                UUID.class);

        // assert
        assertThat(response)
            .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
            .doesNotReturn(null, HttpEntity::getBody);

        assertThat(userRepository.findByUuid(response.getBody()))
            .isPresent()
            .hasValueSatisfying(
                unit ->
                    assertThat(unit)
                        .returns("esther@chavez.com", User::getEmail)
                        .doesNotReturn(null, User::getPassword)
                        .returns(com.mav.openzev.model.UserRole.SUPERUSER, User::getRole));
      }
    }
  }

  @Nested
  class LoginTests {

    //      @Test
    //      void status200() {
    //        // arrange
    //        final LoginDto requestBody = new LoginDto().username("dennis").password("password");
    //
    //        // act
    //        final ResponseEntity<UUID> response =
    //            restTemplate.exchange(
    //                V1_USERS, HttpMethod.POST, new HttpEntity<>(requestBody, null), UUID.class);
    //
    //        // assert
    //        assertThat(response)
    //            .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
    //            .doesNotReturn(null, HttpEntity::getBody);
    //      }
  }

  @Nested
  class GetUserTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_USERS_ID,
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_not_found", ErrorDto::getCode);
    }

    @Test
    void status200() {
      // act
      final ResponseEntity<UserDto> response =
          restTemplate.exchange(
              V1_USERS_ID,
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              UserDto.class,
              UsersModels.UUID);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(UsersModels.UUID, UserDto::getId)
                      .returns(UsersModels.EMAIL, UserDto::getEmail)
                      .returns(UsersModels.FIRST_NAME, UserDto::getFirstName)
                      .returns(UsersModels.LAST_NAME, UserDto::getLastName)
                      .returns(UserRole.SUPERUSER, UserDto::getRole));
    }
  }

  @Nested
  class ChangeUserTests {

    @Test
    void status404() {
      // arrange
      final ModifiableUserDto requestBody =
          new ModifiableUserDto()
              .firstName("Betty")
              .lastName("Fernandes")
              .ownerId(OwnerModels.UUID);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_USERS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_not_found", ErrorDto::getCode);
    }

    @Test
    void status404_owner_not_found() {
      // arrange
      final ModifiableUserDto requestBody =
          new ModifiableUserDto()
              .firstName("Betty")
              .lastName("Fernandes")
              .ownerId(OwnerModels.UUID);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_USERS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UsersModels.UUID);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("owner_not_found", ErrorDto::getCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(OwnerModels.getOwner());

      final ModifiableUserDto requestBody =
          new ModifiableUserDto()
              .firstName("Betty")
              .lastName("Fernandes")
              .ownerId(OwnerModels.UUID);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              V1_USERS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              UUID.class,
              UsersModels.UUID);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(userRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              user ->
                  assertThat(user)
                      .returns(UsersModels.UUID, User::getUuid)
                      .returns("Betty", User::getFirstName)
                      .returns("Fernandes", User::getLastName)
                      .returns(OwnerModels.UUID, u -> u.getOwner().getUuid()));
    }
  }

  @Nested
  class ChangePasswordTests {

    @Test
    void status404() {
      // arrange
      final ModifiablePasswordChangeDto requestBody =
          new ModifiablePasswordChangeDto().oldPassword("password").newPassword("pa55w0rd");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_USERS_ID_PASSWORD,
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_not_found", ErrorDto::getCode);
    }

    @Test
    void status422() {
      // arrange
      final ModifiablePasswordChangeDto requestBody =
          new ModifiablePasswordChangeDto().oldPassword("password!").newPassword("pa55w0rd");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_USERS_ID_PASSWORD,
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UsersModels.UUID);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(HttpEntity::getBody)
          .returns("old_password_invalid", ErrorDto::getCode);
    }

    @Test
    void status204() {
      // arrange
      final ModifiablePasswordChangeDto requestBody =
          new ModifiablePasswordChangeDto().oldPassword("password").newPassword("pa55w0rd");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_USERS_ID_PASSWORD,
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UsersModels.UUID);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);

      assertThat(userRepository.findByUuid(UsersModels.UUID))
          .isPresent()
          .hasValueSatisfying(
              user -> assertThat(passwordEncoder.matches("pa55w0rd", user.getPassword())).isTrue());
    }
  }

  @Nested
  class DeleteUserTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_USERS_ID,
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_not_found", ErrorDto::getCode);
    }

    @Test
    void status204() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_USERS_ID,
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UsersModels.UUID);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);

      assertThat(userRepository.findByUuid(UsersModels.UUID)).isEmpty();
    }
  }

  @Nested
  class ResetPasswordTests {

    @Test
    void status404() {
      // arrange
      final ModifiablePasswordResetDto requestBody =
          new ModifiablePasswordResetDto().newPassword("pa55w0rd");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_SETTINGS_USERS_ID_PASSWORD,
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("user_not_found", ErrorDto::getCode);
    }

    @Test
    void status204() {
      // arrange
      final ModifiablePasswordResetDto requestBody =
          new ModifiablePasswordResetDto().newPassword("pa55w0rd");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V1_SETTINGS_USERS_ID_PASSWORD,
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class,
              UsersModels.UUID);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);

      assertThat(userRepository.findByUuid(UsersModels.UUID))
          .isPresent()
          .hasValueSatisfying(
              user -> assertThat(passwordEncoder.matches("pa55w0rd", user.getPassword())).isTrue());
    }
  }
}
