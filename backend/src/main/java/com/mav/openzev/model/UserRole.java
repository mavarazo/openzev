package com.mav.openzev.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {
  USER("ROLE_USER"),
  SUPERUSER("ROLE_SUPERUSER"),
  ADMIN("ROLE_ADMIN");

  @Getter private final String value;
}
