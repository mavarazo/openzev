package com.mav.openzev.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsersModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("f392c964-9579-4ebd-b1df-d6aff805576a");
  public static final String FIRST_NAME = "Dennis";
  public static final String LAST_NAME = "Nunes";
  public static final String EMAIL = "dennis@nunnes.com";
  public static final UserRole ROLE = UserRole.SUPERUSER;

  public static User get() {
    return User.builder()
        .uuid(UUID)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .password("$2a$10$qS2Mrx6uz0lxp6M8gCpeZ.DZaw6CeIa3dVHYOKYemi0KY5JNaQx7m")
        .role(ROLE)
        .build();
  }
}
