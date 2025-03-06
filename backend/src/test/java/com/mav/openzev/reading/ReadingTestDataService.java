package com.mav.openzev.reading;

import com.mav.openzev.data.AbstractTestDataService;
import com.mav.openzev.data.TestDataManager;
import com.mav.openzev.reading.entity.Reading;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ReadingTestDataService extends AbstractTestDataService {

  public ReadingTestDataService(final TestDataManager testDataManager) {
    super(testDataManager);
  }

  public Reading newReading(final Customize<Reading.ReadingBuilder> customize) {
    final Reading.ReadingBuilder<?, ?> builder =
        Reading.builder().id(UUID.randomUUID()).date(LocalDate.of(2024, 1, 1));
    customize.apply(builder);
    return testDataManager.persist(builder.build());
  }

  public Reading newReading() {
    return newReading(_ -> {});
  }
}
