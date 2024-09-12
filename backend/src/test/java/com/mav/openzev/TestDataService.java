package com.mav.openzev;

import com.mav.openzev.entity.MeterPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
