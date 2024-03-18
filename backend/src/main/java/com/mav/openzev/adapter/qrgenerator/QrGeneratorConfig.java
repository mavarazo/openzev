package com.mav.openzev.adapter.qrgenerator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class QrGeneratorConfig {

  @Value("${openzev.qr-generator.base-url}")
  private String baseUrl;

  @Bean
  public RestClient qrGeneratorClient() {
    return RestClient.builder()
        .baseUrl(baseUrl)
        // .requestFactory(new HttpComponentsClientHttpRequestFactory())
        .messageConverters(configurer -> configurer.add(new BufferedImageHttpMessageConverter()))
        .defaultStatusHandler(
            HttpStatusCode::isError,
            (request, response) -> {
              log.error("Client Error Status " + response.getStatusCode());
              log.error("Client Error Body " + new String(response.getBody().readAllBytes()));
            })
        .build();
  }
}
