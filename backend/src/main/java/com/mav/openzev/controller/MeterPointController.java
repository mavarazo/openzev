package com.mav.openzev.controller;

import com.mav.openzev.api.MeterPointApi;
import com.mav.openzev.api.model.MeterPointDto;
import com.mav.openzev.api.model.ModifiableMeterPointDto;
import com.mav.openzev.mapper.MeterPointToDtoMapper;
import com.mav.openzev.model.ChangeMeterPointCommand;
import com.mav.openzev.model.CreateMeterPointCommand;
import com.mav.openzev.service.MeterPointService;
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
  private final MeterPointToDtoMapper meterPointToDtoMapper;

  @Override
  public ResponseEntity<List<MeterPointDto>> getMeterPoints() {
    final List<MeterPointDto> meterPointDtos =
        meterPointService.getAllMeterPoints().stream()
            .map(meterPointToDtoMapper::mapToMeterPointDto)
            .toList();
    return ResponseEntity.ok(meterPointDtos);
  }

  @Override
  public ResponseEntity<MeterPointDto> getMeterPoint(final UUID meterPointId) {
    final MeterPointDto meterPointDto =
        meterPointToDtoMapper.mapToMeterPointDto(meterPointService.getMeterPoint(meterPointId));
    return ResponseEntity.ok(meterPointDto);
  }

  @Override
  public ResponseEntity<MeterPointDto> createMeterPoint(
      final ModifiableMeterPointDto modifiableMeterPointDto) {
    final CreateMeterPointCommand command =
        meterPointToDtoMapper.mapToCreateMeterPointCommand(modifiableMeterPointDto);
    final MeterPointDto meterPointDto =
        meterPointToDtoMapper.mapToMeterPointDto(meterPointService.createMeterPoint(command));
    return ResponseEntity.status(HttpStatus.CREATED).body(meterPointDto);
  }

  @Override
  public ResponseEntity<MeterPointDto> changeMeterPoint(
      final UUID meterPointId, final ModifiableMeterPointDto modifiableMeterPointDto) {
    final ChangeMeterPointCommand command =
        meterPointToDtoMapper.mapToChangeMeterPointCommand(meterPointId, modifiableMeterPointDto);
    final MeterPointDto meterPointDto =
        meterPointToDtoMapper.mapToMeterPointDto(meterPointService.changeMeterPoint(command));
    return ResponseEntity.status(HttpStatus.OK).body(meterPointDto);
  }

  @Override
  public ResponseEntity<Void> deleteMeterPoint(UUID meterPointId) {
    meterPointService.deleteMeterPoint(meterPointId);
    return ResponseEntity.noContent().build();
  }
}
