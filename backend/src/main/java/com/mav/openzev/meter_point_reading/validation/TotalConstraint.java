package com.mav.openzev.meter_point_reading.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = TotalValidator.class)
public @interface TotalConstraint {

  String message() default "Total is invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
