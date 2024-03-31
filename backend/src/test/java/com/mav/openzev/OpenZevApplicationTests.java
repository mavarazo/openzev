package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest
@MockBeans({@MockBean(JavaMailSenderImpl.class)})
class OpenZevApplicationTests {

  @Test
  void contextLoads() {
    assertThat(true).isTrue();
  }
}
