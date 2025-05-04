package com.mav.openzev.consumption.service;

import com.mav.openzev.consumption.entity.Consumption;
import com.mav.openzev.consumption.entity.Consumption_;
import com.mav.openzev.consumption.model.ChangeConsumptionCommand;
import com.mav.openzev.consumption.model.CreateConsumptionCommand;
import com.mav.openzev.consumption.repository.ConsumptionRepository;
import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.meter_point.repository.MeterPointRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumptionService {

  private final ConsumptionRepository consumptionRepository;
  private final MeterPointRepository meterPointRepository;

  public List<Consumption> getConsumptions() {
    return consumptionRepository.findAll(Sort.by(Consumption_.DATE).ascending());
  }

  public List<Consumption> getConsumptionsByMeterPoints(final UUID meterPointId) {
    return consumptionRepository.findAllByMeterPoint_Id(meterPointId, Sort.by(Consumption_.DATE));
  }

  public Consumption createConsumption(final CreateConsumptionCommand command) {
    final MeterPoint meterPoint = meterPointRepository.findByIdOrFail(command.meterPointId());

    final Consumption consumption =
        Consumption.builder()
            .meterPoint(meterPoint)
            .previousConsumption(
                Optional.ofNullable(command.previousConsumptionId())
                    .map(consumptionRepository::findByIdOrFail)
                    .orElse(null))
            .date(command.date())
            .total(command.total())
            .build();

    return consumptionRepository.save(consumption);
  }

  public Consumption getConsumption(final UUID id) {
    return consumptionRepository.findByIdOrFail(id);
  }

  public Consumption changeConsumption(final ChangeConsumptionCommand command) {
    final Consumption consumption = consumptionRepository.findByIdOrFail(command.id());
    final MeterPoint meterPoint = meterPointRepository.findByIdOrFail(command.meterPointId());

    return consumptionRepository.save(
        consumption.toBuilder()
            .date(command.date())
            .meterPoint(meterPoint)
            .previousConsumption(
                Optional.ofNullable(command.previousConsumptionId())
                    .map(consumptionRepository::findByIdOrFail)
                    .orElse(null))
            .date(command.date())
            .total(command.total())
            .build());
  }

  public void deleteConsumption(final UUID consumptionId) {
    final Consumption consumption = consumptionRepository.findByIdOrFail(consumptionId);
    consumptionRepository.delete(consumption);
  }
}
