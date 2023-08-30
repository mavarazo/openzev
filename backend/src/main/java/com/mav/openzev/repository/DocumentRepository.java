package com.mav.openzev.repository;

import com.mav.openzev.model.Document;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

  Optional<Document> findByUuid(UUID uuid);

  @Query(value = "select d.uuid from OZEV_DOCUMENTS d where d.id = :id", nativeQuery = true)
  Optional<UUID> findByIdUuid(Long id);
}
