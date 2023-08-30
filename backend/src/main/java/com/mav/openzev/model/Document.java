package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "OZEV_DOCUMENTS")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Document extends AbstractAuditEntity {

  @Lob
  @Column(name = "DATA", nullable = false, columnDefinition = "LONGBLOB")
  private byte[] data;
}
