package com.mav.openzev.vendor_invoice.service;

import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import com.mav.openzev.vendor_invoice.entity.VendorInvoice_;
import com.mav.openzev.vendor_invoice.mapper.VendorInvoiceMapper;
import com.mav.openzev.vendor_invoice.model.ChangeVendorInvoiceCommand;
import com.mav.openzev.vendor_invoice.model.CreateVendorInvoiceCommand;
import com.mav.openzev.vendor_invoice.repository.VendorInvoiceRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VendorInvoiceService {

  private final VendorInvoiceRepository vendorInvoiceRepository;
  private final VendorInvoiceMapper vendorInvoiceMapper;

  public List<VendorInvoice> getVendorInvoices() {
    return vendorInvoiceRepository.findAll(Sort.by(VendorInvoice_.PERIOD_FROM).ascending());
  }

  public VendorInvoice createVendorInvoice(final CreateVendorInvoiceCommand command) {
    final VendorInvoice vendorInvoice = vendorInvoiceMapper.mapToVendorInvoice(command);
    return vendorInvoiceRepository.save(vendorInvoice);
  }

  public VendorInvoice getVendorInvoice(final UUID vendorInvoiceId) {
    return vendorInvoiceRepository.findByIdOrFail(vendorInvoiceId);
  }

  public VendorInvoice changeVendorInvoice(final ChangeVendorInvoiceCommand command) {
    final VendorInvoice vendorInvoice = vendorInvoiceRepository.findByIdOrFail(command.id());
    vendorInvoiceMapper.updateVendorInvoice(vendorInvoice, command);
    return vendorInvoiceRepository.save(vendorInvoice);
  }

  public void deleteVendorInvoice(final UUID vendorInvoiceId) {
    final VendorInvoice vendorInvoice = vendorInvoiceRepository.findByIdOrFail(vendorInvoiceId);
    vendorInvoiceRepository.delete(vendorInvoice);
  }
}
