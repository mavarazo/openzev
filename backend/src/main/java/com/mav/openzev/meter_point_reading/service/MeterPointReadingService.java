package com.mav.openzev.meter_point_reading.service;

import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.meter_point.repository.MeterPointRepository;
import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import com.mav.openzev.meter_point_reading.model.ChangeMeterPointReadingCommand;
import com.mav.openzev.meter_point_reading.model.CreateMeterPointReadingCommand;
import com.mav.openzev.meter_point_reading.repository.MeterPointReadingRepository;
import com.mav.openzev.reading.entity.Reading;
import com.mav.openzev.reading.repository.ReadingRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeterPointReadingService {

  private final MeterPointReadingRepository meterPointReadingRepository;
  private final ReadingRepository readingRepository;
  private final MeterPointRepository meterPointRepository;

  public List<MeterPointReading> getMeterPointReadings(final UUID readingId) {
    if (readingId != null) {
      return meterPointReadingRepository.findAllByReadingId(readingId).stream().toList();
    }

    return meterPointReadingRepository.findAll();
  }

  public MeterPointReading createMeterPointReading(final CreateMeterPointReadingCommand command) {
    final Reading reading = readingRepository.findByIdOrFail(command.readingId());
    final MeterPoint meterPoint = meterPointRepository.findByIdOrFail(command.meterPointId());

    return meterPointReadingRepository.save(
        MeterPointReading.builder()
            .reading(reading)
            .meterPoint(meterPoint)
            .peakTariff(command.peakTariff())
            .offPeakTariff(command.offPeakTariff())
            .total(command.total())
            .build());
  }

  public MeterPointReading getMeterPointReading(final UUID meterPointReadingId) {
    return meterPointReadingRepository.findByIdOrFail(meterPointReadingId);
  }

  public MeterPointReading changeMeterPointReading(final ChangeMeterPointReadingCommand command) {
    final MeterPointReading meterPointReading =
        meterPointReadingRepository.findByIdOrFail(command.id());
    final Reading reading = readingRepository.findByIdOrFail(command.readingId());
    final MeterPoint meterPoint = meterPointRepository.findByIdOrFail(command.meterPointId());

    return meterPointReadingRepository.save(
        meterPointReading.toBuilder()
            .reading(reading)
            .meterPoint(meterPoint)
            .peakTariff(command.peakTariff())
            .offPeakTariff(command.offPeakTariff())
            .total(command.total())
            .build());
  }

  public void deleteMeterPointReading(final UUID meterPointReadingId) {
    final MeterPointReading meterPointReading =
        meterPointReadingRepository.findByIdOrFail(meterPointReadingId);
    meterPointReadingRepository.delete(meterPointReading);
  }
}
