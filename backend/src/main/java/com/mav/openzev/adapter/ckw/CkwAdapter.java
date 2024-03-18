package com.mav.openzev.adapter.ckw;

import com.mav.openzev.adapter.ckw.model.ChronoUnit;
import com.mav.openzev.adapter.ckw.model.Consumption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CkwAdapter {

  private final WebClient ckwClient;

  public Mono<List<Consumption>> getConsumption(
      final String contractId,
      final String mpan,
      final LocalDate from,
      final LocalDate upto,
      final ChronoUnit chronoUnit) {
    return ckwClient
        .get()
        .uri(
            uri ->
                uri.path("zp/{contractId}/{mpan}/{from}/{upto}/{chronoUnit}")
                    .build(
                        contractId,
                        mpan,
                        from.format(DateTimeFormatter.BASIC_ISO_DATE),
                        upto.format(DateTimeFormatter.BASIC_ISO_DATE),
                        chronoUnit.getValue()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<Consumption>>() {})
        .log();
  }
}
