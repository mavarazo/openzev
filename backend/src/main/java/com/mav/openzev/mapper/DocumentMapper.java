package com.mav.openzev.mapper;

import static java.util.Objects.nonNull;

import com.mav.openzev.api.model.DocumentDto;
import com.mav.openzev.model.Document;
import java.util.Base64;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface DocumentMapper {

  @Mapping(target = "id", source = "uuid")
  @Mapping(target = "thumbnail", ignore = true)
  DocumentDto mapToDocumentDto(Document document);

  @AfterMapping
  default void afterMapToDocumentDto(
      @MappingTarget final DocumentDto documentDto, final Document document) {
    if (nonNull(document.getThumbnail())) {
      documentDto.setThumbnail(Base64.getEncoder().encodeToString(document.getThumbnail()));
    }
  }
}
