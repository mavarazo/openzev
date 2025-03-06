package com.mav.openzev.reading.service;

import static java.util.Comparator.comparing;

import com.mav.openzev.reading.entity.Reading;
import com.mav.openzev.reading.model.ChangeReadingCommand;
import com.mav.openzev.reading.model.CreateReadingCommand;
import com.mav.openzev.reading.repository.ReadingRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadingService {

  private final ReadingRepository readingRepository;

  public List<Reading> getAllReadings() {
    return readingRepository.findAll().stream().sorted(comparing(Reading::getDate)).toList();
  }

  public Reading getReading(final UUID id) {
    return readingRepository.findByIdOrFail(id);
  }

  public Reading createReading(final CreateReadingCommand command) {
    final Reading reading = Reading.builder().date(command.date()).build();
    return readingRepository.save(reading);
  }

  public Reading changeReading(final ChangeReadingCommand command) {
    final Reading reading =
        readingRepository.findByIdOrFail(command.id()).toBuilder().date(command.date()).build();
    return readingRepository.save(reading);
  }

  public void deleteReading(final UUID readingId) {
    final Reading reading = readingRepository.findByIdOrFail(readingId);
    readingRepository.delete(reading);
  }
}
