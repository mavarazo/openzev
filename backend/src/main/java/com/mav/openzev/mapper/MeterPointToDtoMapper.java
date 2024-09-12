package com.mav.openzev.mapper;

import com.mav.openzev.api.model.MeterPointDto;
import com.mav.openzev.api.model.ModifiableMeterPointDto;
import com.mav.openzev.entity.MeterPoint;
import com.mav.openzev.model.ChangeMeterPointCommand;
import com.mav.openzev.model.CreateMeterPointCommand;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface MeterPointToDtoMapper {

  @Mapping(target = "id", source = "oid")
  MeterPointDto mapToMeterPointDto(MeterPoint meterPoint);

  CreateMeterPointCommand mapToCreateMeterPointCommand(
      ModifiableMeterPointDto modifiableMeterPointDto);

  @Mapping(target = "id", source = "meterPointId")
  ChangeMeterPointCommand mapToChangeMeterPointCommand(
      UUID meterPointId, ModifiableMeterPointDto modifiableMeterPointDto);
}
