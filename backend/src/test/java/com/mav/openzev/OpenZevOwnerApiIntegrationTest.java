package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableOwnerDto;
import com.mav.openzev.api.model.OwnerDto;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.InvoiceModels;
import com.mav.openzev.model.ItemModels;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.OwnerModels;
import com.mav.openzev.model.OwnershipModels;
import com.mav.openzev.model.PaymentModels;
import com.mav.openzev.model.Unit;
import com.mav.openzev.model.UnitModels;
import com.mav.openzev.repository.OwnerRepository;
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
public class OpenZevOwnerApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @Autowired private OwnerRepository ownerRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetOwnersTests {

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(OwnerModels.getOwner());

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
              UriFactory.owners(UUID.randomUUID()),
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
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      testDatabaseService.insert(ItemModels.getItem(invoice));
      testDatabaseService.insert(PaymentModels.getPayment(invoice));

      // act
      final ResponseEntity<OwnerDto> response =
          restTemplate.exchange(
              UriFactory.owners(OwnerModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              OwnerDto.class);

      // assert
      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class CreateOwnerTests {

    @ParameterizedTest
    @RequiredSource(ModifiableOwnerDto.class)
    void status400(final ModifiableOwnerDto requestBody) {
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
    @RequiredSource(ModifiableOwnerDto.class)
    void status400(final ModifiableOwnerDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.owners(OwnerModels.UUID),
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
              UriFactory.owners(UUID.randomUUID()),
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
    void status200() {
      // arrange
      testDatabaseService.insert(OwnerModels.getOwner());

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
              UriFactory.owners(OwnerModels.UUID),
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
              UriFactory.owners(OwnerModels.UUID),
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("owner_not_found", ErrorDto::getCode);
    }

    @Test
    void status422() {
      // arrange
      final Owner owner = testDatabaseService.insert(OwnerModels.getOwner());
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      testDatabaseService.insert(OwnershipModels.getOwnership(owner, unit));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.owners(OwnerModels.UUID),
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("owner_has_ownership", ErrorDto::getCode);
    }

    @Test
    void status204() {
      // arrange
      testDatabaseService.insert(OwnerModels.getOwner());

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.owners(OwnerModels.UUID), HttpMethod.DELETE, HttpEntity.EMPTY, UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
