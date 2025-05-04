package com.mav.openzev.vendor_invoice.mapper;

import com.mav.openzev.common.mapper.MappingConfig;
import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import com.mav.openzev.vendor_invoice.model.ChangeVendorInvoiceCommand;
import com.mav.openzev.vendor_invoice.model.CreateVendorInvoiceCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface VendorInvoiceMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "state", constant = "OPEN")
  @Mapping(target = "items", ignore = true)
  VendorInvoice mapToVendorInvoice(CreateVendorInvoiceCommand createVendorInvoiceCommand);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "state", ignore = true)
  @Mapping(target = "items", ignore = true)
  void updateVendorInvoice(
      @MappingTarget VendorInvoice vendorInvoice,
      ChangeVendorInvoiceCommand changeVendorInvoiceCommand);
}
