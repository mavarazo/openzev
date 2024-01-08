package com.mav.openzev.controller;

import com.mav.openzev.api.ItemApi;
import com.mav.openzev.api.model.ItemDto;
import com.mav.openzev.api.model.ModifiableItemDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.ItemMapper;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Item;
import com.mav.openzev.model.Product;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.repository.ItemRepository;
import com.mav.openzev.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ItemController implements ItemApi {

  private final InvoiceRepository invoiceRepository;
  private final ItemMapper itemMapper;
  private final ItemRepository itemRepository;
  private final ProductRepository productRepository;

  @Override
  @Transactional
  public ResponseEntity<List<ItemDto>> getItems(final UUID invoiceId) {
    final Invoice invoice =
        invoiceRepository
            .findByUuid(invoiceId)
            .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));

    return ResponseEntity.ok(
        invoice.getItems().stream()
            .sorted(Comparator.comparing(item -> item.getProduct().getSubject()))
            .map(itemMapper::mapToItemDto)
            .toList());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createItem(
      final UUID invoiceId, final ModifiableItemDto modifiableItemDto) {
    final Invoice invoice =
        invoiceRepository
            .findByUuid(invoiceId)
            .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));

    final Product product =
        productRepository
            .findByUuid(modifiableItemDto.getProductId())
            .orElseThrow(
                () -> NotFoundException.ofProductNotFound(modifiableItemDto.getProductId()));

    final Item item = itemMapper.mapToItem(modifiableItemDto);
    item.setProduct(product);
    invoice.addItem(item);
    return ResponseEntity.ok(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  public ResponseEntity<UUID> changeItem(
      final UUID itemId, final ModifiableItemDto modifiableItemDto) {
    final Item item =
        itemRepository
            .findByUuid(itemId)
            .orElseThrow(() -> NotFoundException.ofItemNotFound(itemId));

    final Product product =
        productRepository
            .findByUuid(modifiableItemDto.getProductId())
            .orElseThrow(
                () -> NotFoundException.ofProductNotFound(modifiableItemDto.getProductId()));

    itemMapper.updateItem(modifiableItemDto, item);
    item.setProduct(product);
    return ResponseEntity.ok(itemRepository.save(item).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteItem(final UUID itemId) {
    return itemRepository
        .findByUuid(itemId)
        .map(
            item -> {
              itemRepository.delete(item);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(itemId));
  }
}
