package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableSettingsDto;
import com.mav.openzev.api.model.SettingsDto;
import com.mav.openzev.model.Settings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface SettingsMapper {

  @Mapping(target = "id", source = "uuid")
  SettingsDto mapToSettingsDto(Settings settings);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  Settings mapToSettings(ModifiableSettingsDto modifiableSettingsDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  void updateSettings(
      ModifiableSettingsDto modifiableSettingsDto, @MappingTarget Settings settings);
}
