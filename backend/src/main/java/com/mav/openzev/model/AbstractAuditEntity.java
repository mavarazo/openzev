package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AbstractAuditEntity extends AbstractEntity {

  @Column(name = "CREATED", nullable = false, updatable = false)
  @CreatedDate
  private LocalDate created;

  @Column(name = "MODIFIED")
  @LastModifiedDate
  private LocalDate modified;
}
