package com.mav.openzev.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OZEV_USERS")
public class User extends AbstractAuditEntity {

  @Column(name = "ACTIVE")
  @Builder.Default
  private boolean active = true;

  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "EMAIL", unique = true, nullable = false)
  private String email;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "ROLE", nullable = false)
  @Enumerated(value = EnumType.STRING)
  @Builder.Default
  private UserRole role = UserRole.USER;

  @OneToOne
  @JoinColumn(name = "OWNER_ID")
  private Owner owner;
}
