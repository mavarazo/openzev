package com.mav.openzev.vendor_invoice.validation;

import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItem;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TotalValidator implements ConstraintValidator<TotalConstraint, VendorInvoiceItem> {
  @Override
  public boolean isValid(
      final VendorInvoiceItem vendorInvoiceItem,
      final ConstraintValidatorContext constraintValidatorContext) {
    return vendorInvoiceItem.getQuantity() * vendorInvoiceItem.getPrice()
        == vendorInvoiceItem.getTotal();
  }
}
