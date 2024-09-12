package com.mav.openzev.service;

import com.mav.openzev.entity.MeterPoint;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.ChangeMeterPointCommand;
import com.mav.openzev.model.CreateMeterPointCommand;
import com.mav.openzev.repository.MeterPointRepository;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeterPointService {

  private final MeterPointRepository meterPointRepository;

  public List<MeterPoint> getAllMeterPoints() {
    return meterPointRepository.findAll().stream()
        .sorted(Comparator.comparing(MeterPoint::getNumber))
        .toList();
  }

  public MeterPoint getMeterPoint(final UUID id) {
    return meterPointRepository
        .findById(id)
        .orElseThrow(() -> NotFoundException.meterPointNotFound(id));
  }

  public MeterPoint createMeterPoint(final CreateMeterPointCommand command) {
    return meterPointRepository.save(MeterPoint.builder().number(command.number()).build());
  }

  public MeterPoint changeMeterPoint(final ChangeMeterPointCommand command) {
    final MeterPoint meterPoint = getMeterPoint(command.id());
    meterPoint.setNumber(command.number());
    return meterPointRepository.save(meterPoint);
  }
}
