package com.mav.openzev.meter_point_reading;

import com.mav.openzev.data.AbstractTestDataService;
import com.mav.openzev.data.TestDataManager;
import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MeterPointReadingTestDataService extends AbstractTestDataService {

  public MeterPointReadingTestDataService(final TestDataManager testDataManager) {
    super(testDataManager);
  }

  public MeterPointReading newMeterPointReading(
      final Customize<MeterPointReading.MeterPointReadingBuilder> customize) {
    final MeterPointReading.MeterPointReadingBuilder<?, ?> builder =
        MeterPointReading.builder()
            .id(UUID.randomUUID())
            .peakTariff(BigDecimal.TWO)
            .offPeakTariff(BigDecimal.ONE)
            .total(BigDecimal.valueOf(3));
    customize.apply(builder);
    return testDataManager.persist(builder.build());
  }

  public MeterPointReading newMeterPointReading() {
    return newMeterPointReading(_ -> {});
  }
}
