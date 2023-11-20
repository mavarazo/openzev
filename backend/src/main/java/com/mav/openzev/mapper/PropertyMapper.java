package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiablePropertyDto;
import com.mav.openzev.api.model.PropertyDto;
import com.mav.openzev.model.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface PropertyMapper {

  @Mapping(target = "id", source = "uuid")
  PropertyDto mapToPropertyDto(Property property);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "agreements", ignore = true)
  @Mapping(target = "units", ignore = true)
  Property mapToProperty(ModifiablePropertyDto modifiablePropertyDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "agreements", ignore = true)
  @Mapping(target = "units", ignore = true)
  void updateProperty(
      ModifiablePropertyDto modifiablePropertyDto, @MappingTarget Property property);
}
