package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.model.User;
import com.mav.openzev.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User findUserOrFail(final UUID unitId) {
    return userRepository
        .findByUuid(unitId)
        .orElseThrow(() -> NotFoundException.ofUserNotFound(unitId));
  }

  public void changePassword(final User user, final String oldPassword, final String newPassword) {
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw ValidationException.ofOldPasswordInvalid();
    }

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  public void resetPassword(final User user, final String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }
}
