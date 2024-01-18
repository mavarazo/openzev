package com.mav.openzev.repository;

import com.mav.openzev.model.Item;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  Optional<Item> findByUuid(UUID uuid);

  List<Item> findAllByInvoiceUuidOrderByProductSubjectAscNotesAscAmountAsc(UUID invoieUuid);
}
