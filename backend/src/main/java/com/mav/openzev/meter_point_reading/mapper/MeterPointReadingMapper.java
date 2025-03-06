package com.mav.openzev.meter_point_reading.mapper;

import com.mav.openzev.api.model.MeterPointReadingDto;
import com.mav.openzev.common.mapper.MappingConfig;
import com.mav.openzev.meter_point.mapper.MeterPointToDtoMapper;
import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import com.mav.openzev.meter_point_reading.model.ChangeMeterPointReadingCommand;
import com.mav.openzev.meter_point_reading.model.CreateMeterPointReadingCommand;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, uses = MeterPointToDtoMapper.class)
public interface MeterPointReadingMapper {

  @Mapping(target = "readingId", ignore = true)
  @Mapping(target = "meterPointId", ignore = true)
  MeterPointReadingDto mapToMeterPointReadingDto(MeterPointReading meterPointReading);

  CreateMeterPointReadingCommand mapToCreateMeterPointReadingCommand(
      MeterPointReadingDto meterPointReadingDto);

  @Mapping(target = "id", source = "meterPointReadingId")
  ChangeMeterPointReadingCommand mapToChangeMeterPointReadingCommand(
      UUID meterPointReadingId, MeterPointReadingDto meterPointReadingDto);
}
