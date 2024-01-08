package com.mav.openzev.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvoiceModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("414d2033-3b17-4e68-b69e-e483db0dc90b");

  public static Invoice getInvoice() {
    return Invoice.builder()
        .uuid(UUID)
        .unit(UnitModels.getUnit())
        .recipient(OwnerModels.getOwner())
        .status(InvoiceStatus.DRAFT)
        .subject("Lorem ipsum")
        .dueDate(Constants._2024_01_31)
        .build();
  }
}
