package com.mav.openzev.consumption;

import com.mav.openzev.consumption.entity.Consumption;
import com.mav.openzev.data.AbstractTestDataService;
import com.mav.openzev.data.TestDataManager;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ConsumptionTestDataService extends AbstractTestDataService {

  public ConsumptionTestDataService(final TestDataManager testDataManager) {
    super(testDataManager);
  }

  public Consumption newConsumption(
      final Customize<Consumption.ConsumptionBuilder<?, ?>> customize) {
    final Consumption.ConsumptionBuilder<?, ?> builder =
        Consumption.builder().id(UUID.randomUUID()).date(LocalDate.of(2024, 1, 1));
    customize.apply(builder);
    return testDataManager.persist(builder.build());
  }

  public Consumption newConsumption() {
    return newConsumption(_ -> {});
  }
}
