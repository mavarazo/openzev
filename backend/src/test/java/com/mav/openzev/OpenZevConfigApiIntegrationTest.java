package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableZevConfigDto;
import com.mav.openzev.api.model.ModifiableZevRepresentativeConfigDto;
import com.mav.openzev.api.model.ZevConfigDto;
import com.mav.openzev.api.model.ZevRepresentativeConfigDto;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.ZevConfigModels;
import com.mav.openzev.model.ZevRepresentativeConfigModels;
import com.mav.openzev.model.config.ZevConfig;
import com.mav.openzev.model.config.ZevRepresentativeConfig;
import com.mav.openzev.repository.config.ZevConfigRepository;
import com.mav.openzev.repository.config.ZevRepresentativeConfigRepository;
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
public class OpenZevConfigApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private ZevConfigRepository zevConfigRepository;
  @Autowired private ZevRepresentativeConfigRepository zevRepresentativeConfigRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetZevConfigTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.configs_zev(), HttpMethod.GET, HttpEntity.EMPTY, ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("zev_config_not_found", ErrorDto::getCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(ZevConfigModels.getZevConfig());

      // act
      final ResponseEntity<ZevConfigDto> response =
          restTemplate.exchange(
              UriFactory.configs_zev(), HttpMethod.GET, HttpEntity.EMPTY, ZevConfigDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(ZevConfigModels.UUID, ZevConfigDto::getId)
                      .returns(ZevConfigModels.ZEV_STRADUN_30_GRATTAVACHE, ZevConfigDto::getName)
                      .returns(ZevConfigModels.STRADUN, ZevConfigDto::getStreet)
                      .returns(ZevConfigModels._30, ZevConfigDto::getHouseNr)
                      .returns(ZevConfigModels._1624, ZevConfigDto::getPostalCode)
                      .returns(ZevConfigModels.GRATTAVACHE, ZevConfigDto::getCity)
                      .returns(ZevConfigModels._F1DF95E0, ZevConfigDto::getPropertyNr));
    }
  }

  @Nested
  class SaveZevConfigTests {

    @ParameterizedTest
    @RequiredSource(ModifiableZevConfigDto.class)
    void status400(final ModifiableZevConfigDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.configs_zev(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      final ModifiableZevConfigDto requestBody =
          new ModifiableZevConfigDto()
              .name(ZevConfigModels.ZEV_STRADUN_30_GRATTAVACHE)
              .street(ZevConfigModels.STRADUN)
              .houseNr(ZevConfigModels._30)
              .postalCode(ZevConfigModels._1624)
              .city(ZevConfigModels.GRATTAVACHE)
              .propertyNr(ZevConfigModels._F1DF95E0);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.configs_zev(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(zevConfigRepository.findAll())
          .hasSize(1)
          .singleElement()
          .satisfies(
              agreement ->
                  assertThat(agreement)
                      .returns(ZevConfigModels.ZEV_STRADUN_30_GRATTAVACHE, ZevConfig::getName)
                      .returns(ZevConfigModels.STRADUN, ZevConfig::getStreet)
                      .returns(ZevConfigModels._30, ZevConfig::getHouseNr)
                      .returns(ZevConfigModels._1624, ZevConfig::getPostalCode)
                      .returns(ZevConfigModels.GRATTAVACHE, ZevConfig::getCity)
                      .returns(ZevConfigModels._F1DF95E0, ZevConfig::getPropertyNr));
    }

    @Test
    void status200_update() {
      // arrange
      testDatabaseService.insert(ZevConfigModels.getZevConfig());

      final ModifiableZevConfigDto requestBody =
          new ModifiableZevConfigDto()
              .name(ZevConfigModels.ZEV_STRADUN_30_GRATTAVACHE)
              .street(ZevConfigModels.STRADUN)
              .houseNr(ZevConfigModels._30)
              .postalCode(ZevConfigModels._1624)
              .city(ZevConfigModels.GRATTAVACHE)
              .propertyNr(ZevConfigModels._F1DF95E0);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.configs_zev(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(zevConfigRepository.findAll())
          .hasSize(1)
          .singleElement()
          .satisfies(
              agreement ->
                  assertThat(agreement)
                      .returns(ZevConfigModels.ZEV_STRADUN_30_GRATTAVACHE, ZevConfig::getName)
                      .returns(ZevConfigModels.STRADUN, ZevConfig::getStreet)
                      .returns(ZevConfigModels._30, ZevConfig::getHouseNr)
                      .returns(ZevConfigModels._1624, ZevConfig::getPostalCode)
                      .returns(ZevConfigModels.GRATTAVACHE, ZevConfig::getCity)
                      .returns(ZevConfigModels._F1DF95E0, ZevConfig::getPropertyNr));
    }
  }

  @Nested
  class GetZevRepresentativeConfigTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.configs_zev_representative(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("zev_representative_config_not_found", ErrorDto::getCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(ZevRepresentativeConfigModels.getZevRepresentativeConfig());

      // act
      final ResponseEntity<ZevRepresentativeConfigDto> response =
          restTemplate.exchange(
              UriFactory.configs_zev_representative(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ZevRepresentativeConfigDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(
                          ZevRepresentativeConfigModels.UUID, ZevRepresentativeConfigDto::getId)
                      .returns(
                          ZevRepresentativeConfigModels.ANNA,
                          ZevRepresentativeConfigDto::getFirstName)
                      .returns(
                          ZevRepresentativeConfigModels.BARRY,
                          ZevRepresentativeConfigDto::getLastName)
                      .returns(
                          ZevRepresentativeConfigModels.STRADUN,
                          ZevRepresentativeConfigDto::getStreet)
                      .returns(
                          ZevRepresentativeConfigModels.ANNA_BARRY_COM,
                          ZevRepresentativeConfigDto::getEmail)
                      .returns(
                          ZevRepresentativeConfigModels._30, ZevRepresentativeConfigDto::getHouseNr)
                      .returns(
                          ZevRepresentativeConfigModels._1624,
                          ZevRepresentativeConfigDto::getPostalCode)
                      .returns(
                          ZevRepresentativeConfigModels.GRATTAVACHE,
                          ZevRepresentativeConfigDto::getCity)
                      .returns(
                          ZevRepresentativeConfigModels._41415556677,
                          ZevRepresentativeConfigDto::getPhoneNr)
                      .returns(
                          ZevRepresentativeConfigModels._41795556677,
                          ZevRepresentativeConfigDto::getMobileNr));
    }
  }

  @Nested
  class SaveZevRepresentativeConfigTests {

    @ParameterizedTest
    @RequiredSource(ModifiableZevRepresentativeConfigDto.class)
    void status400(final ModifiableZevRepresentativeConfigDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.configs_zev_representative(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      final ModifiableZevRepresentativeConfigDto requestBody =
          new ModifiableZevRepresentativeConfigDto()
              .firstName(ZevRepresentativeConfigModels.ANNA)
              .lastName(ZevRepresentativeConfigModels.BARRY)
              .email(ZevRepresentativeConfigModels.ANNA_BARRY_COM)
              .street(ZevRepresentativeConfigModels.STRADUN)
              .houseNr(ZevRepresentativeConfigModels._30)
              .postalCode(ZevRepresentativeConfigModels._1624)
              .city(ZevRepresentativeConfigModels.GRATTAVACHE)
              .phoneNr(ZevRepresentativeConfigModels._41415556677)
              .mobileNr(ZevRepresentativeConfigModels._41795556677);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.configs_zev_representative(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(zevRepresentativeConfigRepository.findAll())
          .hasSize(1)
          .singleElement()
          .satisfies(
              agreement ->
                  assertThat(agreement)
                      .returns(
                          ZevRepresentativeConfigModels.ANNA, ZevRepresentativeConfig::getFirstName)
                      .returns(
                          ZevRepresentativeConfigModels.BARRY, ZevRepresentativeConfig::getLastName)
                      .returns(
                          ZevRepresentativeConfigModels.ANNA_BARRY_COM,
                          ZevRepresentativeConfig::getEmail)
                      .returns(
                          ZevRepresentativeConfigModels.STRADUN, ZevRepresentativeConfig::getStreet)
                      .returns(
                          ZevRepresentativeConfigModels._30, ZevRepresentativeConfig::getHouseNr)
                      .returns(
                          ZevRepresentativeConfigModels._1624,
                          ZevRepresentativeConfig::getPostalCode)
                      .returns(
                          ZevRepresentativeConfigModels.GRATTAVACHE,
                          ZevRepresentativeConfig::getCity)
                      .returns(
                          ZevRepresentativeConfigModels._41415556677,
                          ZevRepresentativeConfig::getPhoneNr)
                      .returns(
                          ZevRepresentativeConfigModels._41795556677,
                          ZevRepresentativeConfig::getMobileNr));
    }

    @Test
    void status200_update() {
      // arrange
      testDatabaseService.insert(ZevRepresentativeConfigModels.getZevRepresentativeConfig());

      final ModifiableZevRepresentativeConfigDto requestBody =
          new ModifiableZevRepresentativeConfigDto()
              .firstName(ZevRepresentativeConfigModels.ANNA)
              .lastName(ZevRepresentativeConfigModels.BARRY)
              .email(ZevRepresentativeConfigModels.ANNA_BARRY_COM)
              .street(ZevRepresentativeConfigModels.STRADUN)
              .houseNr(ZevRepresentativeConfigModels._30)
              .postalCode(ZevRepresentativeConfigModels._1624)
              .city(ZevRepresentativeConfigModels.GRATTAVACHE)
              .phoneNr(ZevRepresentativeConfigModels._41415556677)
              .mobileNr(ZevRepresentativeConfigModels._41795556677);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.configs_zev_representative(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(zevRepresentativeConfigRepository.findAll())
          .hasSize(1)
          .singleElement()
          .satisfies(
              agreement ->
                  assertThat(agreement)
                      .returns(
                          ZevRepresentativeConfigModels.ANNA, ZevRepresentativeConfig::getFirstName)
                      .returns(
                          ZevRepresentativeConfigModels.BARRY, ZevRepresentativeConfig::getLastName)
                      .returns(
                          ZevRepresentativeConfigModels.ANNA_BARRY_COM,
                          ZevRepresentativeConfig::getEmail)
                      .returns(
                          ZevRepresentativeConfigModels.STRADUN, ZevRepresentativeConfig::getStreet)
                      .returns(
                          ZevRepresentativeConfigModels._30, ZevRepresentativeConfig::getHouseNr)
                      .returns(
                          ZevRepresentativeConfigModels._1624,
                          ZevRepresentativeConfig::getPostalCode)
                      .returns(
                          ZevRepresentativeConfigModels.GRATTAVACHE,
                          ZevRepresentativeConfig::getCity)
                      .returns(
                          ZevRepresentativeConfigModels._41415556677,
                          ZevRepresentativeConfig::getPhoneNr)
                      .returns(
                          ZevRepresentativeConfigModels._41795556677,
                          ZevRepresentativeConfig::getMobileNr));
    }
  }
}
