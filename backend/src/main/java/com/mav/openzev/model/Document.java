package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OZEV_DOCUMENTS")
public class Document extends AbstractAuditEntity {

  @Column(name = "REF_ID", nullable = false)
  private Long refId;

  @Column(name = "REF_TYPE", nullable = false)
  private String refType;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "FILENAME")
  private String filename;

  @Column(name = "MEDIA_TYPE", nullable = false)
  private String mediaType;

  @Lob
  @JdbcType(BinaryJdbcType.class)
  @Column(name = "DATA", nullable = false, columnDefinition = "LONGBLOB")
  private byte[] data;

  @Lob
  @JdbcType(BinaryJdbcType.class)
  @Column(name = "THUMBNAIL", nullable = false, columnDefinition = "LONGBLOB")
  private byte[] thumbnail;
}
