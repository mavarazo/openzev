package com.mav.openzev.consumption.mapper;

import com.mav.openzev.api.model.ConsumptionDto;
import com.mav.openzev.common.mapper.MappingConfig;
import com.mav.openzev.consumption.entity.Consumption;
import com.mav.openzev.consumption.model.ChangeConsumptionCommand;
import com.mav.openzev.consumption.model.CreateConsumptionCommand;
import com.mav.openzev.meter_point.mapper.MeterPointMapper;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class, uses = MeterPointMapper.class)
public interface ConsumptionMapper {

  @Mapping(target = "meterPointId", ignore = true)
  @Mapping(target = "previousConsumptionId", ignore = true)
  ConsumptionDto mapToConsumptionDto(Consumption consumption);

  CreateConsumptionCommand mapToCreateConsumptionCommand(ConsumptionDto consumptionDto);

  @Mapping(target = "id", source = "consumptionId")
  ChangeConsumptionCommand mapToChangeConsumptionCommand(
      UUID consumptionId, ConsumptionDto consumptionDto);
}
