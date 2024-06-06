package com.mav.openzev.controller;


import com.mav.openzev.api.ItemApi;
import com.mav.openzev.api.model.ItemDto;
import com.mav.openzev.api.model.ModifiableItemDto;
import com.mav.openzev.mapper.ItemMapper;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Item;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.repository.ItemRepository;
import com.mav.openzev.service.InvoiceService;
import com.mav.openzev.service.ItemService;
import com.mav.openzev.service.ProductService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ItemController implements ItemApi {

  private final ItemService itemService;
  private final InvoiceService invoiceService;
  private final ProductService productService;

  private final InvoiceRepository invoiceRepository;
  private final ItemMapper itemMapper;
  private final ItemRepository itemRepository;

  @Override
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
    final Invoice invoice = invoiceService.findInvoiceOrFail(invoiceId);

    final Item item = itemMapper.mapToItem(modifiableItemDto);
    productService.findProduct(modifiableItemDto.getProductId()).ifPresent(item::setProduct);
    invoice.addItem(item);
    return ResponseEntity.ok(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  public ResponseEntity<ItemDto> getItem(final UUID itemId) {
    final Item item = itemService.findItemOrFail(itemId);
    return ResponseEntity.ok(itemMapper.mapToItemDto(item));
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> changeItem(
      final UUID itemId, final ModifiableItemDto modifiableItemDto) {
    final Item item = itemService.findItemOrFail(itemId);

    itemMapper.updateItem(modifiableItemDto, item);
    productService.findProduct(modifiableItemDto.getProductId()).ifPresent(item::setProduct);
    return ResponseEntity.ok(itemRepository.save(item).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteItem(final UUID itemId) {
    itemRepository.delete(itemService.findItemOrFail(itemId));
    return ResponseEntity.noContent().build();
  }
}
