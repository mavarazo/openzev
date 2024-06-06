package com.mav.openzev.controller;

import com.mav.openzev.api.UserApi;
import com.mav.openzev.api.model.*;
import com.mav.openzev.mapper.UserMapper;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.User;
import com.mav.openzev.repository.UserRepository;
import com.mav.openzev.service.OwnerService;
import com.mav.openzev.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final UserService userService;
  private final OwnerService ownerService;

  @Override
  public ResponseEntity<List<UserDto>> getUsers() {
    return ResponseEntity.ok(
        userRepository.findAll(Sort.sort(User.class).by(User::getId)).stream()
            .map(userMapper::mapToUserDto)
            .toList());
  }

  @Override
  public ResponseEntity<UUID> inviteUser(final ModifiableInvitationDto modifiableInvitationDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userRepository.save(userMapper.mapToUser(modifiableInvitationDto)).getUuid());
  }

  @Override
  public ResponseEntity<String> login() {
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<UserDto> getUser(final UUID userId) {
    final User user = userService.findUserOrFail(userId);
    return ResponseEntity.ok(userMapper.mapToUserDto(user));
  }

  @Override
  public ResponseEntity<UUID> changeUser(
      final UUID userId, final ModifiableUserDto modifiableUserDto) {
    final User user = userService.findUserOrFail(userId);
    final Optional<Owner> optionalOwner = ownerService.findOwner(modifiableUserDto.getOwnerId());
    userMapper.updateUser(modifiableUserDto, optionalOwner.orElse(null), user);
    return ResponseEntity.ok(userRepository.save(user).getUuid());
  }

  @Override
  public ResponseEntity<Void> changePassword(
      final UUID userId, final ModifiablePasswordChangeDto modifiablePasswordDto) {
    final User user = userService.findUserOrFail(userId);
    userService.changePassword(
        user, modifiablePasswordDto.getOldPassword(), modifiablePasswordDto.getNewPassword());
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteUser(final UUID userId) {
    userRepository.delete(userService.findUserOrFail(userId));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> resetPassword(
      final UUID userId, final ModifiablePasswordResetDto modifiablePasswordDto) {
    final User user = userService.findUserOrFail(userId);
    userService.resetPassword(user, modifiablePasswordDto.getNewPassword());
    return ResponseEntity.noContent().build();
  }
}
