package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Item;
import com.mav.openzev.repository.ItemRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

  private final ItemRepository itemRepository;

  public Item findItemOrFail(final UUID itemId) {
    return itemRepository
        .findByUuid(itemId)
        .orElseThrow(() -> NotFoundException.ofItemNotFound(itemId));
  }
}
