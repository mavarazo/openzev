package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.BankAccountDto;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableBankAccountDto;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.BankAccountModels;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@MockBeans({@MockBean(JavaMailSenderImpl.class)})
public class OpenZevBankAccountApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetBankAccountsTests {

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      // act
      final ResponseEntity<BankAccountDto[]> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              BankAccountDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class GetBankAccountTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(BankAccountModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("bank_account_not_found", ErrorDto::getCode)
          .returns(
              "bank account with id '370f9494-8f2a-4c52-9b6e-5509415d0e4a' not found",
              ErrorDto::getMessage);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      // act
      final ResponseEntity<BankAccountDto> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(BankAccountModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              BankAccountDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class CreateBankAccountTests {

    @ParameterizedTest
    @RequiredSource(ModifiableBankAccountDto.class)
    void status400(final ModifiableBankAccountDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      final ModifiableBankAccountDto requestBody =
          new ModifiableBankAccountDto()
              .iban("563f43db-90cf-43b2-af4d-f6c4c045c8e0")
              .name("Euturpis");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.settings_bank_accounts(),
                  HttpMethod.GET,
                  HttpEntity.EMPTY,
                  BankAccountDto[].class)
              .getBody());
    }
  }

  @Nested
  class ChangeBankAccountTests {

    @ParameterizedTest
    @RequiredSource(ModifiableBankAccountDto.class)
    void status400(final ModifiableBankAccountDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(BankAccountModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiableBankAccountDto requestBody =
          new ModifiableBankAccountDto()
              .iban("563f43db-90cf-43b2-af4d-f6c4c045c8e0")
              .name("Euturpis");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(BankAccountModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(
          BankAccountModels.getBankAccount().toBuilder().uuid(UUID.randomUUID()).build());
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      final ModifiableBankAccountDto requestBody =
          new ModifiableBankAccountDto()
              .iban("563f43db-90cf-43b2-af4d-f6c4c045c8e0")
              .name("Euturpis");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(BankAccountModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.settings_bank_accounts(),
                  HttpMethod.GET,
                  HttpEntity.EMPTY,
                  BankAccountDto[].class)
              .getBody());
    }
  }

  @Nested
  class DeleteBankAccountTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(BankAccountModels.UUID),
              HttpMethod.DELETE,
              null,
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status204() {
      // arrange
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.settings_bank_accounts(BankAccountModels.UUID),
              HttpMethod.DELETE,
              null,
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
