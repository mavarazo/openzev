package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableUnitDto;
import com.mav.openzev.api.model.UnitDto;
import com.mav.openzev.model.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface UnitMapper {

  @Mapping(target = "mpan", source = "meterPointAdministrationNumber")
  UnitDto mapToUnitDto(Unit unit);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "meterPointAdministrationNumber", source = "mpan")
  @Mapping(target = "invoices", ignore = true)
  Unit mapToUnit(ModifiableUnitDto modifiableUnitDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "meterPointAdministrationNumber", source = "mpan")
  @Mapping(target = "invoices", ignore = true)
  void updateUnit(ModifiableUnitDto modifiableUnitDto, @MappingTarget Unit unit);
}
