package com.mav.openzev.meter_point.service;

import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.meter_point.entity.MeterPoint_;
import com.mav.openzev.meter_point.model.ChangeMeterPointCommand;
import com.mav.openzev.meter_point.model.CreateMeterPointCommand;
import com.mav.openzev.meter_point.repository.MeterPointRepository;
import com.mav.openzev.unit.entity.Unit;
import com.mav.openzev.unit.repository.UnitRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeterPointService {

  private final MeterPointRepository meterPointRepository;
  private final UnitRepository unitRepository;

  public List<MeterPoint> getMeterPoints() {
    return meterPointRepository.findAll(Sort.by(MeterPoint_.NUMBER).ascending());
  }

  public MeterPoint getMeterPoint(final UUID id) {
    return meterPointRepository.findByIdOrFail(id);
  }

  public MeterPoint createMeterPoint(final CreateMeterPointCommand command) {
    final Unit unit = unitRepository.findByIdOrFail(command.unitId());
    return meterPointRepository.save(
        MeterPoint.builder().unit(unit).number(command.number()).build());
  }

  public MeterPoint changeMeterPoint(final ChangeMeterPointCommand command) {
    final MeterPoint meterPoint = meterPointRepository.findByIdOrFail(command.id());
    final Unit unit = unitRepository.findByIdOrFail(command.unitId());
    meterPoint.setUnit(unit);
    meterPoint.setNumber(command.number());
    return meterPointRepository.save(meterPoint);
  }

  public void deleteMeterPoint(final UUID meterPointId) {
    final MeterPoint meterPoint = meterPointRepository.findByIdOrFail(meterPointId);
    meterPointRepository.delete(meterPoint);
  }
}
