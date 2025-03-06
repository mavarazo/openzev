package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableReadingDto;
import com.mav.openzev.api.model.ReadingDto;
import com.mav.openzev.entity.Reading;
import com.mav.openzev.model.ChangeReadingCommand;
import com.mav.openzev.model.CreateReadingCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(config = MappingConfig.class, uses = MeterPointToDtoMapper.class)
public interface ReadingToDtoMapper {

  @Mapping(target = "id", source = "oid")
  ReadingDto mapToReadingDto(Reading reading);

  CreateReadingCommand mapToCreateReadingCommand(ModifiableReadingDto modifiableReadingDto);

  @Mapping(target = "id", source = "readingId")
  ChangeReadingCommand mapToChangeReadingCommand(
      UUID readingId, ModifiableReadingDto modifiableReadingDto);
}
