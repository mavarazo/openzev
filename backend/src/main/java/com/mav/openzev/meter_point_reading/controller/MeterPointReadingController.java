package com.mav.openzev.meter_point_reading.controller;

import com.mav.openzev.api.MeterPointReadingApi;
import com.mav.openzev.api.model.MeterPointReadingDto;
import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import com.mav.openzev.meter_point_reading.mapper.MeterPointReadingMapper;
import com.mav.openzev.meter_point_reading.model.ChangeMeterPointReadingCommand;
import com.mav.openzev.meter_point_reading.model.CreateMeterPointReadingCommand;
import com.mav.openzev.meter_point_reading.service.MeterPointReadingService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MeterPointReadingController implements MeterPointReadingApi {

  private final MeterPointReadingService meterPointReadingService;
  private final MeterPointReadingMapper meterPointReadingMapper;

  @Override
  public ResponseEntity<List<MeterPointReadingDto>> getMeterPointReadings(final UUID readingId) {
    final List<MeterPointReadingDto> meterPointReadingDtos =
        meterPointReadingService.getMeterPointReadings(readingId).stream()
            .map(meterPointReadingMapper::mapToMeterPointReadingDto)
            .toList();
    return ResponseEntity.ok(meterPointReadingDtos);
  }

  @Override
  public ResponseEntity<MeterPointReadingDto> createMeterPointReading(
      final MeterPointReadingDto meterPointReadingDto) {
    final CreateMeterPointReadingCommand command =
        meterPointReadingMapper.mapToCreateMeterPointReadingCommand(meterPointReadingDto);
    final MeterPointReading meterPointReading =
        meterPointReadingService.createMeterPointReading(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(meterPointReadingMapper.mapToMeterPointReadingDto(meterPointReading));
  }

  @Override
  public ResponseEntity<MeterPointReadingDto> getMeterPointReading(final UUID meterPointReadingId) {
    final MeterPointReading meterPointReading =
        meterPointReadingService.getMeterPointReading(meterPointReadingId);
    return ResponseEntity.ok(meterPointReadingMapper.mapToMeterPointReadingDto(meterPointReading));
  }

  @Override
  public ResponseEntity<MeterPointReadingDto> changeMeterPointReading(
      final UUID meterPointReadingId, final MeterPointReadingDto meterPointReadingDto) {
    final ChangeMeterPointReadingCommand command =
        meterPointReadingMapper.mapToChangeMeterPointReadingCommand(
            meterPointReadingId, meterPointReadingDto);
    final MeterPointReading meterPointReading =
        meterPointReadingService.changeMeterPointReading(command);
    return ResponseEntity.ok(meterPointReadingMapper.mapToMeterPointReadingDto(meterPointReading));
  }

  @Override
  public ResponseEntity<Void> deleteMeterPointReading(final UUID meterPointReadingId) {
    meterPointReadingService.deleteMeterPointReading(meterPointReadingId);
    return ResponseEntity.noContent().build();
  }
}
