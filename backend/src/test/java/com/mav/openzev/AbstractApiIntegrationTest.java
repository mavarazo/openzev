package com.mav.openzev;

import com.mav.openzev.model.UsersModels;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@MockBeans({@MockBean(JavaMailSenderImpl.class)})
public abstract class AbstractApiIntegrationTest {

  @Autowired private TestDatabaseService testDatabaseService;

  protected HttpHeaders getHttpHeadersWithBasicAuth() {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBasicAuth("dennis@nunnes.com", "password");
    return httpHeaders;
  }

  @BeforeEach
  protected void setUp() {
    testDatabaseService.insert(UsersModels.get());
  }

  @AfterEach
  protected void tearDown() {
    testDatabaseService.truncateAll();
  }
}
