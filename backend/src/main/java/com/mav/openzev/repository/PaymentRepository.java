package com.mav.openzev.repository;

import com.mav.openzev.model.Payment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByUuid(UUID uuid);

  List<Payment> findAllByInvoice_Uuid(UUID invoiceId, Sort sort);
}
