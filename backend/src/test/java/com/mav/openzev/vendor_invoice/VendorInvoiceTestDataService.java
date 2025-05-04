package com.mav.openzev.vendor_invoice;

import com.mav.openzev.data.AbstractTestDataService;
import com.mav.openzev.data.TestDataManager;
import com.mav.openzev.vendor_invoice.entity.State;
import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItem;
import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItemType;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class VendorInvoiceTestDataService extends AbstractTestDataService {

  public VendorInvoiceTestDataService(final TestDataManager testDataManager) {
    super(testDataManager);
  }

  public VendorInvoice newVendorInvoice(
      final Customize<VendorInvoice.VendorInvoiceBuilder<?, ?>> customize) {
    final VendorInvoice.VendorInvoiceBuilder<?, ?> builder =
        VendorInvoice.builder()
            .id(UUID.randomUUID())
            .state(State.OPEN)
            .date(LocalDate.of(2025, 4, 30))
            .periodFrom(LocalDate.of(2025, 1, 1))
            .periodUpto(LocalDate.of(2025, 3, 31))
            .dueDate(LocalDate.of(2025, 5, 30));
    customize.apply(builder);
    return testDataManager.persist(builder.build());
  }

  public VendorInvoice newVendorInvoice() {
    return newVendorInvoice(_ -> {});
  }

  public VendorInvoiceItem newVendorInvoiceItem(
      final Customize<VendorInvoiceItem.VendorInvoiceItemBuilder<?, ?>> customize) {
    final VendorInvoiceItem.VendorInvoiceItemBuilder<?, ?> builder =
        VendorInvoiceItem.builder()
            .id(UUID.randomUUID())
            .type(VendorInvoiceItemType.CHARGE)
            .position(0)
            .name("Einheitstarif Energie")
            .quantity(15000f)
            .unit("kWh")
            .price(1f)
            .total(15000f);
    customize.apply(builder);
    return testDataManager.persist(builder.build());
  }

  public VendorInvoiceItem newVendorInvoiceItem() {
    return newVendorInvoiceItem(_ -> {});
  }
}
