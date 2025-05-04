package com.mav.openzev.vendor_invoice.repository;

import com.mav.openzev.common.exception.NotFoundException;
import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorInvoiceItemRepository extends JpaRepository<VendorInvoiceItem, UUID> {

  @Query(
      "select vii from VendorInvoiceItem vii where vii.vendorInvoice.id = :#{#vendorInvoiceId} order by vii.position")
  List<VendorInvoiceItem> findAllByVendorInvoice_Id(UUID vendorInvoiceId);

  default VendorInvoiceItem findByIdOrFail(final UUID id) {
    return findById(id).orElseThrow(() -> NotFoundException.of(VendorInvoiceItem.class, id));
  }
}
