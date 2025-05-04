package com.mav.openzev.meter_point;

import com.mav.openzev.data.AbstractTestDataService;
import com.mav.openzev.data.TestDataManager;
import com.mav.openzev.meter_point.entity.MeterPoint;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MeterPointTestDataService extends AbstractTestDataService {

  public MeterPointTestDataService(final TestDataManager testDataManager) {
    super(testDataManager);
  }

  public MeterPoint newMeterPoint(final Customize<MeterPoint.MeterPointBuilder<?, ?>> customize) {
    final MeterPoint.MeterPointBuilder<?, ?> builder =
        MeterPoint.builder().id(UUID.randomUUID()).number("34 635 851");
    customize.apply(builder);
    return testDataManager.persist(builder.build());
  }

  public MeterPoint newMeterPoint() {
    return newMeterPoint(_ -> {});
  }
}
