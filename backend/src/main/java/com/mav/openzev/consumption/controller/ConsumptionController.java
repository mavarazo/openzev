package com.mav.openzev.consumption.controller;

import com.mav.openzev.api.ConsumptionsApi;
import com.mav.openzev.api.model.ConsumptionDto;
import com.mav.openzev.consumption.entity.Consumption;
import com.mav.openzev.consumption.mapper.ConsumptionMapper;
import com.mav.openzev.consumption.model.ChangeConsumptionCommand;
import com.mav.openzev.consumption.model.CreateConsumptionCommand;
import com.mav.openzev.consumption.service.ConsumptionService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConsumptionController implements ConsumptionsApi {

  private final ConsumptionService consumptionService;
  private final ConsumptionMapper consumptionMapper;

  @Override
  public ResponseEntity<List<ConsumptionDto>> getConsumptions(final UUID meterPointId) {
    final List<ConsumptionDto> result = new ArrayList<>();

    if (meterPointId == null) {
      result.addAll(
          consumptionService.getConsumptions().stream()
              .map(consumptionMapper::mapToConsumptionDto)
              .toList());
    }

    result.addAll(
        consumptionService.getConsumptionsByMeterPoints(meterPointId).stream()
            .map(consumptionMapper::mapToConsumptionDto)
            .toList());

    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<ConsumptionDto> createConsumption(final ConsumptionDto consumptionDto) {
    final CreateConsumptionCommand command =
        consumptionMapper.mapToCreateConsumptionCommand(consumptionDto);
    final Consumption consumption = consumptionService.createConsumption(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(consumptionMapper.mapToConsumptionDto(consumption));
  }

  @Override
  public ResponseEntity<ConsumptionDto> getConsumption(final UUID consumptionId) {
    final Consumption consumption = consumptionService.getConsumption(consumptionId);
    return ResponseEntity.ok(consumptionMapper.mapToConsumptionDto(consumption));
  }

  @Override
  public ResponseEntity<ConsumptionDto> changeConsumption(
      final UUID consumptionId, final ConsumptionDto consumptionDto) {
    final ChangeConsumptionCommand command =
        consumptionMapper.mapToChangeConsumptionCommand(consumptionId, consumptionDto);
    final Consumption consumption = consumptionService.changeConsumption(command);
    return ResponseEntity.ok(consumptionMapper.mapToConsumptionDto(consumption));
  }

  @Override
  public ResponseEntity<Void> deleteConsumption(final UUID consumptionId) {
    consumptionService.deleteConsumption(consumptionId);
    return ResponseEntity.noContent().build();
  }
}
