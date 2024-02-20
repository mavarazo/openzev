package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.BankAccount;
import com.mav.openzev.repository.BankAccountRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankAccountService {

  private final BankAccountRepository bankAccountRepository;

  public BankAccount findBankAccountOrFail(final UUID bankAccountId) {
    return bankAccountRepository
        .findByUuid(bankAccountId)
        .orElseThrow(() -> NotFoundException.ofBankAccountNotFound(bankAccountId));
  }

  @Transactional
  public BankAccount saveBankAccount(final BankAccount bankAccount) {
    if (bankAccount.isActive()) {
      bankAccountRepository.findAll().stream()
          .filter(b -> !b.getUuid().equals(bankAccount.getUuid()))
          .forEach(b -> b.setActive(false));
    }
    return bankAccountRepository.save(bankAccount);
  }
}
