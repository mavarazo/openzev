package com.mav.openzev.vendor_invoice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TotalValidator.class)
public @interface TotalConstraint {

  String message() default "{vendor-invoice-item.total.invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
