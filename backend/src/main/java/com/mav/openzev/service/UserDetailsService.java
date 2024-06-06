package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.User;
import com.mav.openzev.model.UserRole;
import com.mav.openzev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsService
    implements org.springframework.security.core.userdetails.UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    final User user =
        userRepository
            .findByEmail(username)
            .orElseThrow(() -> NotFoundException.ofUserNotFound(username));

    return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities(evaluateAuthorities(user.getRole()))
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }

  private String evaluateAuthorities(final UserRole userRole) {
    return switch (userRole) {
      case USER -> "ROLE_USER";
      case SUPERUSER -> "ROLE_SUPERUSER";
      case ADMIN -> "ROLE_ADMIN";
    };
  }
}
