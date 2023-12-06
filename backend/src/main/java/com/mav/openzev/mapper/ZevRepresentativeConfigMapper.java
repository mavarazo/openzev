package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableZevRepresentativeConfigDto;
import com.mav.openzev.api.model.ZevRepresentativeConfigDto;
import com.mav.openzev.model.config.ZevRepresentativeConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface ZevRepresentativeConfigMapper {

  @Mapping(target = "id", source = "uuid")
  ZevRepresentativeConfigDto mapToZevRepresentativeConfigDto(
      ZevRepresentativeConfig zevRepresentativeConfig);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  ZevRepresentativeConfig mapToZevRepresentativeConfig(
      ModifiableZevRepresentativeConfigDto modifiableZevRepresentativeConfigDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  void updateZevRepresentativeConfig(
      ModifiableZevRepresentativeConfigDto modifiableZevRepresentativeConfigDto,
      @MappingTarget ZevRepresentativeConfig zevRepresentativeConfig);
}
