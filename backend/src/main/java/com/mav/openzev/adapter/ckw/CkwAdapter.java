package com.mav.openzev.adapter.ckw;

import static java.util.Objects.nonNull;

import com.mav.openzev.adapter.ckw.model.ChronoUnit;
import com.mav.openzev.adapter.ckw.model.Consumption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CkwAdapter {

  private final RestClient ckwClient;

  public List<Consumption> getConsumption(
      final String contractId,
      final String mpan,
      final LocalDate from,
      final LocalDate upto,
      final ChronoUnit chronoUnit) {
    final ResponseEntity<List<Consumption>> response =
        ckwClient
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
            .toEntity(new ParameterizedTypeReference<>() {});

    final List<Consumption> body = response.getBody();
    if (response.getStatusCode().is2xxSuccessful() && nonNull(body)) {
      return body;
    }
    return Collections.emptyList();
  }
}
