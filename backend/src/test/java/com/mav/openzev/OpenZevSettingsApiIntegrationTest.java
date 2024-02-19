package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.*;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.Settings;
import com.mav.openzev.model.SettingsModels;
import com.mav.openzev.repository.RepresentativeRepository;
import com.mav.openzev.repository.SettingsRepository;
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
public class OpenZevSettingsApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;

  @Autowired private SettingsRepository settingsRepository;
  @Autowired private RepresentativeRepository representativeRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetSettingsTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings(), HttpMethod.GET, HttpEntity.EMPTY, ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("settings_not_found", ErrorDto::getCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(SettingsModels.getSettings());

      // act
      final ResponseEntity<SettingsDto> response =
          restTemplate.exchange(
              UriFactory.settings(), HttpMethod.GET, HttpEntity.EMPTY, SettingsDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(SettingsModels.UUID, SettingsDto::getId)
                      .returns(SettingsModels.ZEV_STRADUN_30_GRATTAVACHE, SettingsDto::getName)
                      .returns(SettingsModels.STRADUN, SettingsDto::getStreet)
                      .returns(SettingsModels._30, SettingsDto::getHouseNr)
                      .returns(SettingsModels._1624, SettingsDto::getPostalCode)
                      .returns(SettingsModels.GRATTAVACHE, SettingsDto::getCity)
                      .returns(SettingsModels._F1DF95E0, SettingsDto::getPropertyNr));
    }
  }

  @Nested
  class SaveSettingsTests {

    @ParameterizedTest
    @RequiredSource(ModifiableSettingsDto.class)
    void status400(final ModifiableSettingsDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.settings(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      final ModifiableSettingsDto requestBody =
          new ModifiableSettingsDto()
              .name(SettingsModels.ZEV_STRADUN_30_GRATTAVACHE)
              .street(SettingsModels.STRADUN)
              .houseNr(SettingsModels._30)
              .postalCode(SettingsModels._1624)
              .city(SettingsModels.GRATTAVACHE)
              .propertyNr(SettingsModels._F1DF95E0);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.settings(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(settingsRepository.findAll())
          .hasSize(1)
          .singleElement()
          .satisfies(
              agreement ->
                  assertThat(agreement)
                      .returns(SettingsModels.ZEV_STRADUN_30_GRATTAVACHE, Settings::getName)
                      .returns(SettingsModels.STRADUN, Settings::getStreet)
                      .returns(SettingsModels._30, Settings::getHouseNr)
                      .returns(SettingsModels._1624, Settings::getPostalCode)
                      .returns(SettingsModels.GRATTAVACHE, Settings::getCity)
                      .returns(SettingsModels._F1DF95E0, Settings::getPropertyNr));
    }

    @Test
    void status200_update() {
      // arrange
      testDatabaseService.insert(SettingsModels.getSettings());

      final ModifiableSettingsDto requestBody =
          new ModifiableSettingsDto()
              .name(SettingsModels.ZEV_STRADUN_30_GRATTAVACHE)
              .street(SettingsModels.STRADUN)
              .houseNr(SettingsModels._30)
              .postalCode(SettingsModels._1624)
              .city(SettingsModels.GRATTAVACHE)
              .propertyNr(SettingsModels._F1DF95E0);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.settings(),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(settingsRepository.findAll())
          .hasSize(1)
          .singleElement()
          .satisfies(
              agreement ->
                  assertThat(agreement)
                      .returns(SettingsModels.ZEV_STRADUN_30_GRATTAVACHE, Settings::getName)
                      .returns(SettingsModels.STRADUN, Settings::getStreet)
                      .returns(SettingsModels._30, Settings::getHouseNr)
                      .returns(SettingsModels._1624, Settings::getPostalCode)
                      .returns(SettingsModels.GRATTAVACHE, Settings::getCity)
                      .returns(SettingsModels._F1DF95E0, Settings::getPropertyNr));
    }
  }
}
