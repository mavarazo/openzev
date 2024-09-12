package com.mav.openzev.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class AbstractEntity {

  @Id
  @Column(name = "OID", nullable = false, unique = true)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private UUID oid;

  @PrePersist
  protected void onPrePersist() {
    if (ObjectUtils.isEmpty(oid)) {
      oid = UUID.randomUUID();
    }
  }
}
