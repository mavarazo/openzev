package com.mav.openzev;

import com.mav.openzev.data.TestDataManager;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public abstract class AbstractApiIntegrationTest {

  @Autowired protected TestRestTemplate restTemplate;

  @Autowired protected TestDataManager testDataManager;

  @AfterEach
  protected void tearDown() {
    testDataManager.truncateAll();
  }
}
