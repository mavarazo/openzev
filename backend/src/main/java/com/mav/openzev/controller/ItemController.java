package com.mav.openzev.controller;

import static java.util.Objects.isNull;

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
import java.util.List;
import java.util.Optional;
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
    return ResponseEntity.ok(
        itemRepository
            .findAllByInvoiceUuidOrderByProductSubjectAscNotesAscAmountAsc(invoiceId)
            .stream()
            .map(itemMapper::mapToItemDto)
            .toList());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createItem(
      final UUID invoiceId, final ModifiableItemDto modifiableItemDto) {
    final Invoice invoice = findInvoiceOrFail(invoiceId);

    final Item item = itemMapper.mapToItem(modifiableItemDto);
    findProduct(modifiableItemDto.getProductId()).ifPresent(item::setProduct);
    invoice.addItem(item);
    return ResponseEntity.ok(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  public ResponseEntity<ItemDto> getItem(final UUID itemId) {
    final Item item = findItemOrFail(itemId);
    return ResponseEntity.ok(itemMapper.mapToItemDto(item));
  }

  @Override
  public ResponseEntity<UUID> changeItem(
      final UUID itemId, final ModifiableItemDto modifiableItemDto) {
    final Item item = findItemOrFail(itemId);

    itemMapper.updateItem(modifiableItemDto, item);
    findProduct(modifiableItemDto.getProductId()).ifPresent(item::setProduct);
    return ResponseEntity.ok(itemRepository.save(item).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteItem(final UUID itemId) {
    itemRepository.delete(findItemOrFail(itemId));
    return ResponseEntity.noContent().build();
  }

  private Item findItemOrFail(final UUID itemId) {
    return itemRepository
        .findByUuid(itemId)
        .orElseThrow(() -> NotFoundException.ofItemNotFound(itemId));
  }

  private Invoice findInvoiceOrFail(final UUID invoiceId) {
    return invoiceRepository
        .findByUuid(invoiceId)
        .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));
  }

  private Optional<Product> findProduct(final UUID productId) {
    return isNull(productId)
        ? Optional.empty()
        : Optional.ofNullable(
            productRepository
                .findByUuid(productId)
                .orElseThrow(() -> NotFoundException.ofProductNotFound(productId)));
  }
}
