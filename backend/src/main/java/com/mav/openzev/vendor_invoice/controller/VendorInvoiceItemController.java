package com.mav.openzev.vendor_invoice.controller;

import com.mav.openzev.api.VendorInvoiceItemsApi;
import com.mav.openzev.api.model.VendorInvoiceItemDto;
import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItem;
import com.mav.openzev.vendor_invoice.mapper.VendorInvoiceItemMapper;
import com.mav.openzev.vendor_invoice.model.ChangeVendorInvoiceItemCommand;
import com.mav.openzev.vendor_invoice.model.CreateVendorInvoiceItemCommand;
import com.mav.openzev.vendor_invoice.service.VendorInvoiceItemService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VendorInvoiceItemController implements VendorInvoiceItemsApi {

  private final VendorInvoiceItemService vendorInvoiceItemService;
  private final VendorInvoiceItemMapper vendorInvoiceItemMapper;

  @Override
  public ResponseEntity<List<VendorInvoiceItemDto>> getVendorInvoiceItems(
      final UUID vendorInvoiceId) {
    final List<VendorInvoiceItemDto> vendorInvoiceItemDtos =
        vendorInvoiceItemService.getItems(vendorInvoiceId).stream()
            .map(vendorInvoiceItemMapper::mapToVendorInvoiceItemDto)
            .toList();
    return ResponseEntity.ok(vendorInvoiceItemDtos);
  }

  @Override
  public ResponseEntity<VendorInvoiceItemDto> createVendorInvoiceItem(
      final UUID vendorInvoiceId, final VendorInvoiceItemDto vendorInvoiceItemDto) {
    final CreateVendorInvoiceItemCommand command =
        vendorInvoiceItemMapper.mapToCreateVendorInvoiceItemCommand(
            vendorInvoiceId, vendorInvoiceItemDto);
    final VendorInvoiceItem vendorInvoiceItem = vendorInvoiceItemService.createItem(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(vendorInvoiceItemMapper.mapToVendorInvoiceItemDto(vendorInvoiceItem));
  }

  @Override
  public ResponseEntity<VendorInvoiceItemDto> changeVendorInvoiceItem(
      final UUID vendorInvoiceId,
      final UUID vendorInvoiceItemId,
      final VendorInvoiceItemDto vendorInvoiceItemDto) {
    final ChangeVendorInvoiceItemCommand command =
        vendorInvoiceItemMapper.mapToChangeVendorInvoiceItemCommand(
            vendorInvoiceId, vendorInvoiceItemId, vendorInvoiceItemDto);
    final VendorInvoiceItem vendorInvoiceItem = vendorInvoiceItemService.changeItem(command);
    return ResponseEntity.ok(vendorInvoiceItemMapper.mapToVendorInvoiceItemDto(vendorInvoiceItem));
  }

  @Override
  public ResponseEntity<Void> deleteVendorInvoiceItem(
      final UUID vendorInvoiceId, final UUID vendorInvoiceItemId) {
    vendorInvoiceItemService.deleteItem(vendorInvoiceItemId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
