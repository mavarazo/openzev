package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableZevConfigDto;
import com.mav.openzev.api.model.ZevConfigDto;
import com.mav.openzev.model.config.ZevConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface ZevConfigMapper {

  @Mapping(target = "id", source = "uuid")
  ZevConfigDto mapToZevConfigDto(ZevConfig zevConfig);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  ZevConfig mapToZevConfig(ModifiableZevConfigDto modifiableZevConfigDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  void updateZevConfig(
      ModifiableZevConfigDto modifiableZevConfigDto, @MappingTarget ZevConfig zevConfig);
}
