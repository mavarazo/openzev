package com.mav.openzev.vendor_invoice.service;

import static java.util.Objects.requireNonNullElse;

import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItem;
import com.mav.openzev.vendor_invoice.model.ChangeVendorInvoiceItemCommand;
import com.mav.openzev.vendor_invoice.model.CreateVendorInvoiceItemCommand;
import com.mav.openzev.vendor_invoice.repository.VendorInvoiceItemRepository;
import com.mav.openzev.vendor_invoice.repository.VendorInvoiceRepository;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VendorInvoiceItemService {

  private final VendorInvoiceRepository vendorInvoiceRepository;
  private final VendorInvoiceItemRepository vendorInvoiceItemRepository;

  public List<VendorInvoiceItem> getItems(final UUID vendorInvoiceId) {
    return vendorInvoiceItemRepository.findAllByVendorInvoice_Id(vendorInvoiceId);
  }

  @Transactional
  public VendorInvoiceItem createItem(final CreateVendorInvoiceItemCommand command) {
    final VendorInvoice vendorInvoice =
        vendorInvoiceRepository.findByIdOrFail(command.vendorInvoiceId());

    final VendorInvoiceItem vendorInvoiceItem =
        VendorInvoiceItem.builder()
            .type(command.type())
            .position(requireNonNullElse(command.position(), nextPosition(vendorInvoice)))
            .name(command.name())
            .description(command.description())
            .quantity(command.quantity())
            .unit(command.unit())
            .price(command.price())
            .total(command.total())
            .build();

    vendorInvoice.addItem(vendorInvoiceItem);
    return vendorInvoiceItemRepository.save(vendorInvoiceItem);
  }

  @Transactional
  public VendorInvoiceItem changeItem(final ChangeVendorInvoiceItemCommand command) {
    final VendorInvoice vendorInvoice =
        vendorInvoiceRepository.findByIdOrFail(command.vendorInvoiceId());

    final VendorInvoiceItem vendorInvoiceItem =
        vendorInvoiceItemRepository.findByIdOrFail(command.vendorInvoiceItemId()).toBuilder()
            .type(command.type())
            .position(requireNonNullElse(command.position(), nextPosition(vendorInvoice)))
            .name(command.name())
            .description(command.description())
            .quantity(command.quantity())
            .unit(command.unit())
            .price(command.price())
            .total(command.total())
            .build();

    return vendorInvoiceItemRepository.save(vendorInvoiceItem);
  }

  public void deleteItem(final UUID vendorInvoiceItemId) {
    final VendorInvoiceItem vendorInvoiceItem =
        vendorInvoiceItemRepository.findByIdOrFail(vendorInvoiceItemId);
    vendorInvoiceItemRepository.delete(vendorInvoiceItem);
  }

  private int nextPosition(final VendorInvoice vendorInvoice) {
    final OptionalInt optionalPosition =
        vendorInvoice.getItems().stream().mapToInt(VendorInvoiceItem::getPosition).max();
    return optionalPosition.isPresent() ? optionalPosition.getAsInt() + 1 : 0;
  }
}
