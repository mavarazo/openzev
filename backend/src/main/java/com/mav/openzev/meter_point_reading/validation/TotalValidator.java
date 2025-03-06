package com.mav.openzev.meter_point_reading.validation;

import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public class TotalValidator implements ConstraintValidator<TotalConstraint, MeterPointReading> {

  @Override
  public boolean isValid(
      final MeterPointReading meterPointReading,
      final ConstraintValidatorContext constraintValidatorContext) {
    final BigDecimal expectedTotal =
        Stream.of(meterPointReading.getPeakTariff(), meterPointReading.getOffPeakTariff())
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    final boolean isValid = expectedTotal.equals(meterPointReading.getTotal());
    if (!isValid) {

      final HibernateConstraintValidatorContext hibernateConstraintValidatorContext =
          constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);

      hibernateConstraintValidatorContext.addMessageParameter(
          "peakTariff", meterPointReading.getPeakTariff());
      hibernateConstraintValidatorContext.addMessageParameter(
          "offPeakTariff", meterPointReading.getOffPeakTariff());
      hibernateConstraintValidatorContext.addMessageParameter(
          "total", meterPointReading.getTotal());
    }
    return isValid;
  }
}
