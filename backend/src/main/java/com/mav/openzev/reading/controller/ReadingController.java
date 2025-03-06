package com.mav.openzev.reading.controller;

import com.mav.openzev.api.ReadingApi;
import com.mav.openzev.api.model.ReadingDto;
import com.mav.openzev.reading.entity.Reading;
import com.mav.openzev.reading.mapper.ReadingToDtoMapper;
import com.mav.openzev.reading.model.ChangeReadingCommand;
import com.mav.openzev.reading.model.CreateReadingCommand;
import com.mav.openzev.reading.service.ReadingService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReadingController implements ReadingApi {

  private final ReadingService readingService;
  private final ReadingToDtoMapper readingToDtoMapper;

  @Override
  public ResponseEntity<List<ReadingDto>> getReadings() {
    final List<ReadingDto> readingDtos =
        readingService.getAllReadings().stream().map(readingToDtoMapper::mapToReadingDto).toList();
    return ResponseEntity.ok(readingDtos);
  }

  @Override
  public ResponseEntity<ReadingDto> createReading(final ReadingDto readingDto) {
    final CreateReadingCommand command = readingToDtoMapper.mapToCreateReadingCommand(readingDto);
    final Reading reading = readingService.createReading(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(readingToDtoMapper.mapToReadingDto(reading));
  }

  @Override
  public ResponseEntity<ReadingDto> getReading(final UUID readingId) {
    final ReadingDto readingDto =
        readingToDtoMapper.mapToReadingDto(readingService.getReading(readingId));
    return ResponseEntity.ok(readingDto);
  }

  @Override
  public ResponseEntity<ReadingDto> changeReading(
      final UUID readingId, final ReadingDto readingDto) {
    final ChangeReadingCommand command =
        readingToDtoMapper.mapToChangeReadingCommand(readingId, readingDto);
    final Reading reading = readingService.changeReading(command);
    return ResponseEntity.status(HttpStatus.OK).body(readingToDtoMapper.mapToReadingDto(reading));
  }

  @Override
  public ResponseEntity<Void> deleteReading(final UUID readingId) {
    readingService.deleteReading(readingId);
    return ResponseEntity.noContent().build();
  }
}
