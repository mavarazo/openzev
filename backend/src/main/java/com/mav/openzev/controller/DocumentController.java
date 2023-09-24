package com.mav.openzev.controller;

import com.mav.openzev.api.DocumentApi;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Document;
import com.mav.openzev.repository.DocumentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DocumentController implements DocumentApi {

  private final DocumentRepository documentRepository;

  @Override
  public ResponseEntity<Resource> getDocument(final UUID documentId) {
    final Document document =
        documentRepository
            .findByUuid(documentId)
            .orElseThrow(() -> NotFoundException.ofDocumentNotFound(documentId));

    return ResponseEntity.ok(new ByteArrayResource(document.getData()));
  }

  @Override
  public ResponseEntity<Void> deleteDocument(final UUID documentId) {
    final Document document =
        documentRepository
            .findByUuid(documentId)
            .orElseThrow(() -> NotFoundException.ofDocumentNotFound(documentId));

    documentRepository.delete(document);
    return ResponseEntity.noContent().build();
  }
}
