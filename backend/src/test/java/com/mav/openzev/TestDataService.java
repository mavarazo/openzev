package com.mav.openzev;

import com.mav.openzev.entity.MeterPoint;
import com.mav.openzev.entity.Reading;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestDataService {

  private final TestDatabaseService testDatabaseService;

  public interface Customize<T> {
    void apply(T t);
  }

  public MeterPoint newMeterPoint(final Customize<MeterPoint> customize) {
    final MeterPoint meterPoint = defaultMeterPoint();
    customize.apply(meterPoint);
    return testDatabaseService.persist(meterPoint);
  }

  public MeterPoint newMeterPoint() {
    return newMeterPoint(_ -> {});
  }

  private static MeterPoint defaultMeterPoint() {
    return MeterPoint.builder().oid(UUID.randomUUID()).number("34 635 851").build();
  }

  public Reading newReading(final Customize<Reading> customize) {
    Reading reading = defaultReading();
    customize.apply(reading);
    return testDatabaseService.persist(reading);
  }

  public Reading newReading() {
    return newReading(_ -> {});
  }

  private static Reading defaultReading() {
    return Reading.builder().oid(UUID.randomUUID())
            .date(LocalDate.of(2024, 1, 1))
            .meterPoint(new MeterPoint())
            .peakTariff(BigDecimal.valueOf(100))
            .offPeakTariff(BigDecimal.valueOf(50))
            .total(BigDecimal.valueOf(150))
            .build();
  }
}
