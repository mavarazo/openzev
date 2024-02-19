package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableRepresentativeDto;
import com.mav.openzev.api.model.RepresentativeDto;
import com.mav.openzev.model.Representative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface RepresentativeMapper {

  @Mapping(target = "id", source = "uuid")
  RepresentativeDto mapToRepresentativeDto(Representative representative);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  Representative mapToRepresentative(ModifiableRepresentativeDto modifiableRepresentativeDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  void updateRepresentative(
      ModifiableRepresentativeDto modifiableRepresentativeDto,
      @MappingTarget Representative representative);
}
