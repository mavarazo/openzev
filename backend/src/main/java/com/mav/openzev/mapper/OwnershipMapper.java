package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableOwnershipDto;
import com.mav.openzev.api.model.OwnershipDto;
import com.mav.openzev.model.Ownership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface OwnershipMapper {

  @Mapping(target = "unit", source = "unit.uuid")
  @Mapping(target = "user", source = "user.uuid")
  OwnershipDto mapToOwnershipDto(Ownership ownership);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "unit", ignore = true)
  @Mapping(target = "user", ignore = true)
  Ownership mapToOwnership(ModifiableOwnershipDto modifiableOwnershipDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "unit", ignore = true)
  @Mapping(target = "user", ignore = true)
  void updateOwnership(
      ModifiableOwnershipDto modifiableOwnershipDto, @MappingTarget Ownership ownership);
}
