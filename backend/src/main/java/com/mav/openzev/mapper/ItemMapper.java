package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ItemDto;
import com.mav.openzev.api.model.ModifiableItemDto;
import com.mav.openzev.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface ItemMapper {

  @Mapping(target = "id", source = "uuid")
  @Mapping(target = "invoiceId", ignore = true)
  @Mapping(target = "productId", ignore = true)
  ItemDto mapToItemDto(Item invoice);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "invoice", ignore = true)
  @Mapping(target = "product", ignore = true)
  Item mapToItem(ModifiableItemDto modifiableItemDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "invoice", ignore = true)
  @Mapping(target = "product", ignore = true)
  void updateItem(ModifiableItemDto modifiableItemDto, @MappingTarget Item item);
}
