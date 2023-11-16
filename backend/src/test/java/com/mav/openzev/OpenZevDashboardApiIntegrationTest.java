package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.AccountingOverviewDto;
import com.mav.openzev.api.model.OwnerOverviewDto;
import com.mav.openzev.api.model.UnitOverviewDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
public class OpenZevDashboardApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private com.mav.openzev.TestDatabaseService testDatabaseService;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetAccountingOverviewTests {

    @Test
    void status200() {
      // act
      final ResponseEntity<AccountingOverviewDto> response =
          restTemplate.exchange(
              UriFactory.dashboard_accountings(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              AccountingOverviewDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns(0, AccountingOverviewDto::getTotal));
    }
  }

  @Nested
  class GetUnitOverviewTests {

    @Test
    void status200() {
      // act
      final ResponseEntity<UnitOverviewDto> response =
          restTemplate.exchange(
              UriFactory.dashboard_units(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              UnitOverviewDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns(0, UnitOverviewDto::getTotal));
    }
  }

  @Nested
  class GetOwnerOverviewTests {

    @Test
    void status200() {
      // act
      final ResponseEntity<OwnerOverviewDto> response =
          restTemplate.exchange(
              UriFactory.dashboard_owners(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              OwnerOverviewDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).returns(0, OwnerOverviewDto::getTotal));
    }
  }
}
