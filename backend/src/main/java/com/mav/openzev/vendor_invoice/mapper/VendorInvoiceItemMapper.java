package com.mav.openzev.vendor_invoice.mapper;

import com.mav.openzev.api.model.VendorInvoiceItemDto;
import com.mav.openzev.common.mapper.MappingConfig;
import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItem;
import com.mav.openzev.vendor_invoice.model.ChangeVendorInvoiceItemCommand;
import com.mav.openzev.vendor_invoice.model.CreateVendorInvoiceItemCommand;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface VendorInvoiceItemMapper {

  VendorInvoiceItemDto mapToVendorInvoiceItemDto(VendorInvoiceItem vendorInvoiceItem);

  CreateVendorInvoiceItemCommand mapToCreateVendorInvoiceItemCommand(
      UUID vendorInvoiceId, VendorInvoiceItemDto vendorInvoiceItemDto);

  ChangeVendorInvoiceItemCommand mapToChangeVendorInvoiceItemCommand(
      UUID vendorInvoiceId, UUID vendorInvoiceItemId, VendorInvoiceItemDto vendorInvoiceItemDto);
}
