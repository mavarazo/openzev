package com.mav.openzev.adapter.qrgenerator;

import java.net.http.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class QrGeneratorConfig {

  @Value("${openzev.qr-generator.base-url}")
  private String baseUrl;

  @Bean
  public RestClient qrGeneratorClient() {
    final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    final JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(client);

    return RestClient.builder()
        .requestFactory(requestFactory)
        .baseUrl(baseUrl)
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
