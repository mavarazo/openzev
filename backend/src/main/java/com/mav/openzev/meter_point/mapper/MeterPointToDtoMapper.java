package com.mav.openzev.meter_point.mapper;

import com.mav.openzev.api.model.MeterPointDto;
import com.mav.openzev.common.mapper.MappingConfig;
import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.meter_point.model.ChangeMeterPointCommand;
import com.mav.openzev.meter_point.model.CreateMeterPointCommand;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface MeterPointToDtoMapper {

  @Mapping(target = "unitId", ignore = true)
  MeterPointDto mapToMeterPointDto(MeterPoint meterPoint);

  CreateMeterPointCommand mapToCreateMeterPointCommand(MeterPointDto modifiableMeterPointDto);

  @Mapping(target = "id", source = "meterPointId")
  ChangeMeterPointCommand mapToChangeMeterPointCommand(
      UUID meterPointId, MeterPointDto modifiableMeterPointDto);
}
