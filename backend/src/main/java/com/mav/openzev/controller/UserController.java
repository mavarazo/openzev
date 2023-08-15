package com.mav.openzev.controller;

import com.mav.openzev.api.UserApi;
import com.mav.openzev.api.model.ModifiableUserDto;
import com.mav.openzev.api.model.UserDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.UserMapper;
import com.mav.openzev.model.User;
import com.mav.openzev.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  @Override
  public ResponseEntity<List<UserDto>> getUsers() {
    return ResponseEntity.ok(
        userRepository.findAll(Sort.sort(User.class).by(User::getUuid)).stream()
            .map(userMapper::mapToUserDto)
            .toList());
  }

  @Override
  public ResponseEntity<UserDto> getUser(final UUID userId) {
    return ResponseEntity.ok(
        userRepository
            .findByUuid(userId)
            .map(userMapper::mapToUserDto)
            .orElseThrow(() -> NotFoundException.ofUserNotFound(userId)));
  }

  @Override
  public ResponseEntity<UUID> createUser(final ModifiableUserDto modifiableUserDto) {
    final User user = userRepository.save(userMapper.mapToUser(modifiableUserDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(user.getUuid());
  }

  @Override
  public ResponseEntity<UUID> changeUser(
      final UUID userId, final ModifiableUserDto modifiableUserDto) {
    return userRepository
        .findByUuid(userId)
        .map(
            user -> {
              userMapper.updateUser(modifiableUserDto, user);
              return ResponseEntity.ok(userRepository.save(user).getUuid());
            })
        .orElseThrow(() -> NotFoundException.ofUserNotFound(userId));
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteUser(final UUID userId) {
    final User user =
        userRepository
            .findByUuid(userId)
            .orElseThrow(() -> NotFoundException.ofUserNotFound(userId));

    if (!user.getOwnerships().isEmpty()) {
      throw ValidationException.ofUserHasOwnership(user);
    }

    userRepository.delete(user);
    return ResponseEntity.noContent().<Void>build();
  }
}
