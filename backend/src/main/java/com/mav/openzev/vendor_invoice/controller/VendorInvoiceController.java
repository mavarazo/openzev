package com.mav.openzev.vendor_invoice.controller;

import com.mav.openzev.api.VendorInvoicesApi;
import com.mav.openzev.api.model.VendorInvoiceDto;
import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import com.mav.openzev.vendor_invoice.mapper.VendorInvoiceDtoMapper;
import com.mav.openzev.vendor_invoice.model.ChangeVendorInvoiceCommand;
import com.mav.openzev.vendor_invoice.model.CreateVendorInvoiceCommand;
import com.mav.openzev.vendor_invoice.service.VendorInvoiceService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VendorInvoiceController implements VendorInvoicesApi {

  private final VendorInvoiceService vendorInvoiceService;
  private final VendorInvoiceDtoMapper vendorInvoiceDtoMapper;

  @Override
  public ResponseEntity<List<VendorInvoiceDto>> getVendorInvoices() {
    final List<VendorInvoiceDto> vendorInvoiceDtos =
        vendorInvoiceService.getVendorInvoices().stream()
            .map(vendorInvoiceDtoMapper::mapToVendorInvoiceDto)
            .toList();
    return ResponseEntity.ok(vendorInvoiceDtos);
  }

  @Override
  public ResponseEntity<VendorInvoiceDto> createVendorInvoice(
      final VendorInvoiceDto vendorInvoiceDto) {
    final CreateVendorInvoiceCommand command =
        vendorInvoiceDtoMapper.mapToCreateVendorInvoiceCommand(vendorInvoiceDto);
    final VendorInvoice vendorInvoice = vendorInvoiceService.createVendorInvoice(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(vendorInvoiceDtoMapper.mapToVendorInvoiceDto(vendorInvoice));
  }

  @Override
  public ResponseEntity<VendorInvoiceDto> getVendorInvoice(final UUID vendorInvoiceId) {
    final VendorInvoice vendorInvoice = vendorInvoiceService.getVendorInvoice(vendorInvoiceId);
    return ResponseEntity.ok(vendorInvoiceDtoMapper.mapToVendorInvoiceDto(vendorInvoice));
  }

  @Override
  public ResponseEntity<VendorInvoiceDto> changeVendorInvoice(
      final UUID vendorInvoiceId, final VendorInvoiceDto vendorInvoiceDto) {
    final ChangeVendorInvoiceCommand command =
        vendorInvoiceDtoMapper.mapToChangeVendorInvoiceDto(vendorInvoiceId, vendorInvoiceDto);
    final VendorInvoice vendorInvoice = vendorInvoiceService.changeVendorInvoice(command);
    return ResponseEntity.ok(vendorInvoiceDtoMapper.mapToVendorInvoiceDto(vendorInvoice));
  }

  @Override
  public ResponseEntity<Void> deleteVendorInvoice(final UUID vendorInvoiceId) {
    vendorInvoiceService.deleteVendorInvoice(vendorInvoiceId);
    return ResponseEntity.noContent().build();
  }
}
