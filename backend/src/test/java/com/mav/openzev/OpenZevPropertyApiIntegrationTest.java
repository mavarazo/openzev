package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiablePropertyDto;
import com.mav.openzev.api.model.PropertyDto;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.Property;
import com.mav.openzev.repository.PropertyRepository;
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
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenZevPropertyApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private PropertyRepository propertyRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetPropertiesTests {

    @Test
    @Sql(scripts = {"/db/test-data/properties.sql"})
    void status200() {
      // act
      final ResponseEntity<PropertyDto[]> response =
          restTemplate.exchange(
              UriFactory.properties(), HttpMethod.GET, HttpEntity.EMPTY, PropertyDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetPropertyTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.properties("67e8c925-abdf-418c-be74-c84332082f62"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("property_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/properties.sql",
        })
    void status200() {
      // act
      final ResponseEntity<PropertyDto> response =
          restTemplate.exchange(
              UriFactory.properties("67e8c925-abdf-418c-be74-c84332082f62"),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              PropertyDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(
                          UUID.fromString("67e8c925-abdf-418c-be74-c84332082f62"),
                          PropertyDto::getId)
                      .returns(true, PropertyDto::getActive)
                      .returns("Stradun", PropertyDto::getStreet)
                      .returns("30", PropertyDto::getHouseNr)
                      .returns("1624", PropertyDto::getPostalCode)
                      .returns("Grattavache", PropertyDto::getCity));
    }
  }

  @Nested
  class CreatePropertyTests {

    @ParameterizedTest
    @RequiredSource(ModifiablePropertyDto.class)
    void status400(final ModifiablePropertyDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.properties(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status201() {
      // arrange
      final ModifiablePropertyDto requestBody =
          new ModifiablePropertyDto()
              .street("Stradun")
              .houseNr("30")
              .postalCode("1624")
              .city("Grattavache");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.properties(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(propertyRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              property ->
                  assertThat(property)
                      .returns(true, Property::isActive)
                      .returns("Stradun", Property::getStreet)
                      .returns("30", Property::getHouseNr)
                      .returns("1624", Property::getPostalCode)
                      .returns("Grattavache", Property::getCity));
    }
  }

  @Nested
  class ChangePropertyTests {

    @ParameterizedTest
    @RequiredSource(ModifiablePropertyDto.class)
    void status400(final ModifiablePropertyDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.properties("67e8c925-abdf-418c-be74-c84332082f62"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiablePropertyDto requestBody =
          new ModifiablePropertyDto()
              .street("Stradun")
              .houseNr("30")
              .postalCode("1624")
              .city("Grattavache");

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.properties("67e8c925-abdf-418c-be74-c84332082f62"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("property_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/properties.sql",
        })
    void status200() {
      // arrange
      final ModifiablePropertyDto requestBody =
          new ModifiablePropertyDto()
              .street("Stradun")
              .houseNr("30")
              .postalCode("1624")
              .city("Grattavache");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.properties("67e8c925-abdf-418c-be74-c84332082f62"),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(propertyRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              property ->
                  assertThat(property)
                      .returns(
                          UUID.fromString("67e8c925-abdf-418c-be74-c84332082f62"),
                          Property::getUuid)
                      .returns(true, Property::isActive)
                      .returns("Stradun", Property::getStreet)
                      .returns("30", Property::getHouseNr)
                      .returns("1624", Property::getPostalCode)
                      .returns("Grattavache", Property::getCity));
    }
  }

  @Nested
  class DeletePropertyTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.properties("67e8c925-abdf-418c-be74-c84332082f62"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("property_not_found", ErrorDto::getCode);
    }

    @Test
    @Sql(
        scripts = {
          "/db/test-data/properties.sql",
          "/db/test-data/units.sql",
        })
    void status422_unit() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.properties("67e8c925-abdf-418c-be74-c84332082f62"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("property_has_unit", ErrorDto::getCode);
    }

    @Test
    @Sql(scripts = {"/db/test-data/properties.sql"})
    void status204() {
      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.properties("67e8c925-abdf-418c-be74-c84332082f62"),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
