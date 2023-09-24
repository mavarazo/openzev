package com.mav.openzev.repository;

import com.mav.openzev.model.Document;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

  Optional<Document> findByUuid(UUID uuid);

  List<Document> findAllByRefIdAndRefType(Long refId, String refType, Sort sort);
}
