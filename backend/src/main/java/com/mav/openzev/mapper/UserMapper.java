package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableUserDto;
import com.mav.openzev.api.model.UserDto;
import com.mav.openzev.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface UserMapper {

  UserDto mapToUserDto(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "ownerships", ignore = true)
  User mapToUser(ModifiableUserDto modifiableUserDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "ownerships", ignore = true)
  void updateUser(ModifiableUserDto modifiableUserDto, @MappingTarget User user);
}
