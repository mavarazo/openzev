package com.mav.openzev.adapter.ckw;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class CkwAdapterConfig {

  @Value("${openzev.ckw.base-url}")
  private String baseUrl;

  @Bean
  RestClient ckwClient() {
    return RestClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultStatusHandler(
            HttpStatusCode::isError,
            (request, response) -> {
              log.error("Client Error Status " + response.getStatusCode());
              log.error("Client Error Body " + new String(response.getBody().readAllBytes()));
            })
        .build();
  }
}
