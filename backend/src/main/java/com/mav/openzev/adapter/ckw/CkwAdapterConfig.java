package com.mav.openzev.adapter.ckw;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class CkwAdapterConfig {

  @Value("${openzev.ckw.base-url}")
  private String baseUrl;

  @Bean
  WebClient ckwClient() {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", baseUrl))
        .filters(
            exchangeFilterFunctions -> {
              exchangeFilterFunctions.add(logRequest());
              exchangeFilterFunctions.add(logResponse());
            })
        .build();
  }

  private static ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(
        clientRequest -> {
          if (log.isDebugEnabled()) {
            final StringBuilder sb =
                new StringBuilder("Request: \n")
                    .append(clientRequest.method())
                    .append(" ")
                    .append(clientRequest.url());
            clientRequest
                .headers()
                .forEach(
                    (k, v) ->
                        v.forEach(value -> sb.append("\n").append(k).append(":").append(value)));
            log.debug(sb.toString());
          }
          return Mono.just(clientRequest);
        });
  }

  private static ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(
        clientResponse -> {
          if (log.isDebugEnabled()) {
            final StringBuilder sb =
                new StringBuilder("Response: \n")
                    .append("Status: ")
                    .append(clientResponse.statusCode());
            clientResponse
                .headers()
                .asHttpHeaders()
                .forEach(
                    (k, v) ->
                        v.forEach(value -> sb.append("\n").append(k).append(":").append(value)));
            log.debug(sb.toString());
          }
          return Mono.just(clientResponse);
        });
  }
}
