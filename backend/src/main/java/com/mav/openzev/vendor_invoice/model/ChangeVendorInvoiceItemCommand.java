package com.mav.openzev.vendor_invoice.model;

import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItemType;
import java.util.UUID;

public record ChangeVendorInvoiceItemCommand(
    UUID vendorInvoiceItemId,
    UUID vendorInvoiceId,
    VendorInvoiceItemType type,
    Integer position,
    String name,
    String description,
    Float quantity,
    String unit,
    Float price,
    Float total) {}
