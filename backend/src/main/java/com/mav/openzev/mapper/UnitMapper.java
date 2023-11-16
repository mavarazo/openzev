package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableUnitDto;
import com.mav.openzev.api.model.UnitDto;
import com.mav.openzev.model.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface UnitMapper {

  @Mapping(target = "id", source = "uuid")
  @Mapping(target = "propertyId", source = "property.uuid")
  @Mapping(target = "mpan", source = "meterPointAdministrationNumber")
  UnitDto mapToUnitDto(Unit unit);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "meterPointAdministrationNumber", source = "mpan")
  @Mapping(target = "property", ignore = true)
  @Mapping(target = "ownerships", ignore = true)
  @Mapping(target = "invoices", ignore = true)
  Unit mapToUnit(ModifiableUnitDto modifiableUnitDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "meterPointAdministrationNumber", source = "mpan")
  @Mapping(target = "property", ignore = true)
  @Mapping(target = "ownerships", ignore = true)
  @Mapping(target = "invoices", ignore = true)
  void updateUnit(ModifiableUnitDto modifiableUnitDto, @MappingTarget Unit unit);
}
