package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableProductDto;
import com.mav.openzev.api.model.ProductDto;
import com.mav.openzev.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface ProductMapper {

  @Mapping(target = "id", source = "uuid")
  ProductDto mapToProductDto(Product product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  Product mapToProduct(ModifiableProductDto modifiableProductDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  void updateProduct(ModifiableProductDto modifiableProductDto, @MappingTarget Product product);
}
