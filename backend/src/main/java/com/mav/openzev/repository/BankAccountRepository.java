package com.mav.openzev.repository;

import com.mav.openzev.model.BankAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

  Optional<BankAccount> findByUuid(UUID uuid);

  @Query("select b from BankAccount b where b.active = true")
  Optional<BankAccount> findActive();
}
