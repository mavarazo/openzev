package com.mav.openzev.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
@MappedSuperclass
public abstract class AbstractEntity {

  @Id
  @Column(name = "id", nullable = false, unique = true)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private UUID id;

  @PrePersist
  protected void onPrePersist() {
    if (ObjectUtils.isEmpty(id)) {
      id = UUID.randomUUID();
    }
  }
}
