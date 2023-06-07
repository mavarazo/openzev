package com.mav.openzev.controller;

import static java.util.Objects.isNull;

import com.mav.openzev.adapter.impl.CkwAdapterImpl;
import com.mav.openzev.adapter.model.ChronoUnit;
import com.mav.openzev.adapter.model.Consumption;
import com.mav.openzev.api.V1Api;
import com.mav.openzev.api.model.ChronoEnum;
import com.mav.openzev.api.model.ConsumptionDto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DefaultController implements V1Api {

  private final CkwAdapterImpl ckwAdapter;

  @Override
  public ResponseEntity<List<ConsumptionDto>> getUserConsumptions(
          String userId, LocalDate from, LocalDate upto, ChronoEnum chrono) {
    ChronoUnit chronoUnit = mapChronoEnumToChronoUnit(chrono);

    List<Consumption> consumptions =
        ckwAdapter
            .getConsumption(userId, "CH1003601234500000000000005107583", from, upto, chronoUnit)
            .block();

    if (isNull(consumptions) || consumptions.isEmpty()) {
      return ResponseEntity.ok(Collections.emptyList());
    }

    return ResponseEntity.ok(
        consumptions.stream().map(this::mapConsumptionToConsumptionDto).toList());
  }

  private static ChronoUnit mapChronoEnumToChronoUnit(ChronoEnum value) {
    return switch (value) {
      case YEAR -> ChronoUnit.YEAR;
      case MONTH -> ChronoUnit.MONTH;
      case DAY -> ChronoUnit.DAY;
      case HOUR -> ChronoUnit.HOUR;
      case QUARTER_HOUR -> ChronoUnit.QUARTER_HOUR;
    };
  }

  private ConsumptionDto mapConsumptionToConsumptionDto(Consumption value) {
    return new ConsumptionDto()
        .from(value.zeitstempelVonUtc())
        .upto(value.zeitstempelBisUtc())
        .amountHighTariff(value.betragHt())
        .amountLowTariff(value.betragNt())
        .maxPowerBillable(value.maxLeistungFaktura())
        .maxPowerPhysically(value.maxLeistungPhysisch())
        .quantityHighTariff(value.mengeFakturiertHt())
        .quantityLowTariff(value.mengeFakturiertNt())
        .quantityTotal(value.mengePhysikalisch());
  }
}
