package com.mav.openzev.vendor_invoice.model;

import java.time.LocalDate;

public record CreateVendorInvoiceCommand(
    LocalDate date, LocalDate periodFrom, LocalDate periodUpto, LocalDate dueDate) {}
