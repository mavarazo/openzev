package com.mav.openzev.vendor_invoice.mapper;

import com.mav.openzev.api.model.VendorInvoiceDto;
import com.mav.openzev.common.mapper.MappingConfig;
import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import com.mav.openzev.vendor_invoice.model.ChangeVendorInvoiceCommand;
import com.mav.openzev.vendor_invoice.model.CreateVendorInvoiceCommand;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface VendorInvoiceDtoMapper {

  VendorInvoiceDto mapToVendorInvoiceDto(VendorInvoice vendorInvoice);

  CreateVendorInvoiceCommand mapToCreateVendorInvoiceCommand(VendorInvoiceDto vendorInvoiceDto);

  @Mapping(target = "id", source = "vendorInvoiceId")
  ChangeVendorInvoiceCommand mapToChangeVendorInvoiceDto(
      UUID vendorInvoiceId, VendorInvoiceDto vendorInvoiceDto);
}
