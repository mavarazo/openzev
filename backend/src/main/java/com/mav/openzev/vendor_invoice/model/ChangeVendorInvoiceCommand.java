package com.mav.openzev.vendor_invoice.model;

import java.time.LocalDate;
import java.util.UUID;

public record ChangeVendorInvoiceCommand(
    UUID id, LocalDate date, LocalDate periodFrom, LocalDate periodUpto, LocalDate dueDate) {}
