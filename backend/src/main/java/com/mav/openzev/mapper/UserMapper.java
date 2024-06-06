package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiableInvitationDto;
import com.mav.openzev.api.model.ModifiableUserDto;
import com.mav.openzev.api.model.UserDto;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(config = MappingConfig.class)
public abstract class UserMapper {

  @Autowired private PasswordEncoder passwordEncoder;

  @Mapping(target = "id", source = "uuid")
  @Mapping(target = "ownerId", source = "owner.uuid")
  public abstract UserDto mapToUserDto(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "owner", ignore = true)
  public abstract User mapToUser(ModifiableInvitationDto modifiableInvitationDto);

  @AfterMapping
  protected void afterMapToUser(
      final ModifiableInvitationDto modifiableInvitationDto,
      @MappingTarget final User.UserBuilder<?, ?> user) {
    user.password(passwordEncoder.encode(modifiableInvitationDto.getPassword()));
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "firstName", source = "modifiableUserDto.firstName")
  @Mapping(target = "lastName", source = "modifiableUserDto.lastName")
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "owner", source = "owner")
  public abstract void updateUser(
      ModifiableUserDto modifiableUserDto, Owner owner, @MappingTarget User user);
}
