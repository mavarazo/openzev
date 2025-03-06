package com.mav.openzev.reading.mapper;

import com.mav.openzev.api.model.ReadingDto;
import com.mav.openzev.common.mapper.MappingConfig;
import com.mav.openzev.reading.entity.Reading;
import com.mav.openzev.reading.model.ChangeReadingCommand;
import com.mav.openzev.reading.model.CreateReadingCommand;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface ReadingToDtoMapper {

  ReadingDto mapToReadingDto(Reading reading);

  CreateReadingCommand mapToCreateReadingCommand(ReadingDto modifiableReadingDto);

  @Mapping(target = "id", source = "readingId")
  ChangeReadingCommand mapToChangeReadingCommand(UUID readingId, ReadingDto modifiableReadingDto);
}
