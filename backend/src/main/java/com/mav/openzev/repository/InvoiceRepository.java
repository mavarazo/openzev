package com.mav.openzev.repository;

import com.mav.openzev.model.Invoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

  Optional<Invoice> findByUuid(UUID uuid);

  List<Invoice> findAllByAccounting_Uuid(UUID accountingUuid);
}
