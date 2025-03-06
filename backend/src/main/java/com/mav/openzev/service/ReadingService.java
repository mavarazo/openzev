package com.mav.openzev.service;

import static java.util.Comparator.comparing;

import com.mav.openzev.entity.Reading;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationExcpetion;
import com.mav.openzev.model.ChangeReadingCommand;
import com.mav.openzev.model.CreateReadingCommand;
import com.mav.openzev.repository.ReadingRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadingService {

  private final ReadingRepository readingRepository;

  private final MeterPointService meterPointService;

  public List<Reading> getAllReadings() {
    return readingRepository.findAll().stream()
        .sorted(
            comparing(Reading::getDate)
                .thenComparing(reading -> reading.getMeterPoint().getNumber()))
        .toList();
  }

  public Reading getReading(final UUID id) {
    return getReadingOrFail(id);
  }

  public Reading createReading(final CreateReadingCommand command) {
    final Reading reading =
        Reading.builder()
            .meterPoint(meterPointService.getMeterPoint(command.meterPointId()))
            .date(command.date())
            .peakTariff(command.peakTariff())
            .offPeakTariff(command.offPeakTariff())
            .total(command.total())
            .build();

    assertTotal(reading);
    return readingRepository.save(reading);
  }

  public Reading changeReading(final ChangeReadingCommand command) {
    final Reading reading =
        getReadingOrFail(command.id()).toBuilder()
            .meterPoint(meterPointService.getMeterPoint(command.meterPointId()))
            .date(command.date())
            .peakTariff(command.peakTariff())
            .offPeakTariff(command.offPeakTariff())
            .total(command.total())
            .build();

    assertTotal(reading);
    return readingRepository.save(reading);
  }

  private Reading getReadingOrFail(final UUID id) {
    return readingRepository.findById(id).orElseThrow(() -> NotFoundException.readingNotFound(id));
  }

  private static void assertTotal(final Reading reading) {
    final BigDecimal expectedTotal =
        Stream.of(reading.getPeakTariff(), reading.getOffPeakTariff())
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (!expectedTotal.equals(reading.getTotal())) {
      throw ValidationExcpetion.readingTotalIncorrect(reading);
    }
  }

  public void deleteReading(final UUID readingId) {
    final Reading reading = getReadingOrFail(readingId);
    readingRepository.delete(reading);
  }
}
