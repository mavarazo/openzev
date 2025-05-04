package com.mav.openzev.vendor_invoice.repository;

import com.mav.openzev.common.exception.NotFoundException;
import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorInvoiceRepository extends JpaRepository<VendorInvoice, UUID> {

  default VendorInvoice findByIdOrFail(final UUID id) {
    return findById(id).orElseThrow(() -> NotFoundException.of(VendorInvoice.class, id));
  }
}
