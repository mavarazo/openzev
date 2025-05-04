package com.mav.openzev.meter_point.controller;

import com.mav.openzev.api.MeterPointApi;
import com.mav.openzev.api.model.MeterPointDto;
import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.meter_point.mapper.MeterPointMapper;
import com.mav.openzev.meter_point.model.ChangeMeterPointCommand;
import com.mav.openzev.meter_point.model.CreateMeterPointCommand;
import com.mav.openzev.meter_point.service.MeterPointService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MeterPointController implements MeterPointApi {

  private final MeterPointService meterPointService;
  private final MeterPointMapper meterPointMapper;

  @Override
  public ResponseEntity<List<MeterPointDto>> getMeterPoints() {
    final List<MeterPointDto> meterPointDtos =
        meterPointService.getMeterPoints().stream()
            .map(meterPointMapper::mapToMeterPointDto)
            .toList();
    return ResponseEntity.ok(meterPointDtos);
  }

  @Override
  public ResponseEntity<MeterPointDto> getMeterPoint(final UUID meterPointId) {
    final MeterPoint meterPoint = meterPointService.getMeterPoint(meterPointId);
    return ResponseEntity.ok(meterPointMapper.mapToMeterPointDto(meterPoint));
  }

  @Override
  public ResponseEntity<MeterPointDto> createMeterPoint(final MeterPointDto meterPointDto) {
    final CreateMeterPointCommand command =
        meterPointMapper.mapToCreateMeterPointCommand(meterPointDto);
    final MeterPoint meterPoint = meterPointService.createMeterPoint(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(meterPointMapper.mapToMeterPointDto(meterPoint));
  }

  @Override
  public ResponseEntity<MeterPointDto> changeMeterPoint(
      final UUID meterPointId, final MeterPointDto meterPointDto) {
    final ChangeMeterPointCommand command =
        meterPointMapper.mapToChangeMeterPointCommand(meterPointId, meterPointDto);
    final MeterPoint meterPoint = meterPointService.changeMeterPoint(command);
    return ResponseEntity.ok(meterPointMapper.mapToMeterPointDto(meterPoint));
  }

  @Override
  public ResponseEntity<Void> deleteMeterPoint(final UUID meterPointId) {
    meterPointService.deleteMeterPoint(meterPointId);
    return ResponseEntity.noContent().build();
  }
}
