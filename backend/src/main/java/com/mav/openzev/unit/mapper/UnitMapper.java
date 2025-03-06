package com.mav.openzev.unit.mapper;

import com.mav.openzev.api.model.UnitDto;
import com.mav.openzev.common.mapper.MappingConfig;
import com.mav.openzev.unit.entity.Unit;
import com.mav.openzev.unit.model.ChangeUnitCommand;
import com.mav.openzev.unit.model.CreateUnitCommand;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface UnitMapper {

  UnitDto mapToUnitDto(Unit unit);

  CreateUnitCommand mapToCreateUnitCommand(UnitDto unitDto);

  @Mapping(target = "id", source = "unitId")
  ChangeUnitCommand mapToChangeUnitCommand(UUID unitId, UnitDto unitDto);
}
