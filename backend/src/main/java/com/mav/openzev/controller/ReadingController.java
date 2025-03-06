package com.mav.openzev.controller;

import com.mav.openzev.api.ReadingApi;
import com.mav.openzev.api.model.ModifiableReadingDto;
import com.mav.openzev.api.model.ReadingDto;
import com.mav.openzev.mapper.ReadingToDtoMapper;
import com.mav.openzev.model.ChangeReadingCommand;
import com.mav.openzev.model.CreateReadingCommand;
import com.mav.openzev.service.ReadingService;
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
  public ResponseEntity<ReadingDto> createReading(ModifiableReadingDto modifiableReadingDto) {
    final CreateReadingCommand command =
        readingToDtoMapper.mapToCreateReadingCommand(modifiableReadingDto);
    final ReadingDto readingDto =
        readingToDtoMapper.mapToReadingDto(readingService.createReading(command));
    return ResponseEntity.status(HttpStatus.CREATED).body(readingDto);
  }

  @Override
  public ResponseEntity<ReadingDto> getReading(UUID readingId) {
    final ReadingDto readingDto =
        readingToDtoMapper.mapToReadingDto(readingService.getReading(readingId));
    return ResponseEntity.ok(readingDto);
  }

  @Override
  public ResponseEntity<ReadingDto> changeReading(
      UUID readingId, ModifiableReadingDto modifiableReadingDto) {
    final ChangeReadingCommand command =
        readingToDtoMapper.mapToChangeReadingCommand(readingId, modifiableReadingDto);
    final ReadingDto readingDto =
        readingToDtoMapper.mapToReadingDto(readingService.changeReading(command));
    return ResponseEntity.status(HttpStatus.OK).body(readingDto);
  }

  @Override
  public ResponseEntity<Void> deleteReading(UUID readingId) {
    readingService.deleteReading(readingId);
    return ResponseEntity.noContent().build();
  }
}
