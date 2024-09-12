package com.mav.openzev;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public abstract class AbstractApiIntegrationTest {

  @Autowired protected TestRestTemplate restTemplate;

  @Autowired protected TestDatabaseService testDatabaseService;

  @AfterEach
  protected void tearDown() {
    testDatabaseService.truncateAll();
  }
}
