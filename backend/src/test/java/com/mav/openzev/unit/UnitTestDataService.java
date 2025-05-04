package com.mav.openzev.unit;

import com.mav.openzev.data.AbstractTestDataService;
import com.mav.openzev.data.TestDataManager;
import com.mav.openzev.unit.entity.Unit;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UnitTestDataService extends AbstractTestDataService {

  public UnitTestDataService(final TestDataManager testDataManager) {
    super(testDataManager);
  }

  public Unit newUnit(final Customize<Unit.UnitBuilder<?, ?>> customize) {
    final Unit.UnitBuilder<?, ?> builder =
        Unit.builder().id(UUID.randomUUID()).number("1234").firstName("Foo").lastName("Bar");
    customize.apply(builder);
    return testDataManager.persist(builder.build());
  }

  public Unit newUnit() {
    return newUnit(_ -> {});
  }
}
