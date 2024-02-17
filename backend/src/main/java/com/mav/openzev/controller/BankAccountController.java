package com.mav.openzev.controller;

import com.mav.openzev.api.BankAccountApi;
import com.mav.openzev.api.model.BankAccountDto;
import com.mav.openzev.api.model.ModifiableBankAccountDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.BankAccountMapper;
import com.mav.openzev.model.BankAccount;
import com.mav.openzev.repository.BankAccountRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BankAccountController implements BankAccountApi {

  private final BankAccountRepository bankAccountRepository;
  private final BankAccountMapper bankAccountMapper;

  @Override
  public ResponseEntity<List<BankAccountDto>> getBankAccounts() {
    return ResponseEntity.ok(
        bankAccountRepository
            .findAll(Sort.sort(BankAccount.class).by(BankAccount::getName).ascending())
            .stream()
            .map(bankAccountMapper::mapToBankAccountDto)
            .toList());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createBankAccount(
      final ModifiableBankAccountDto modifiableBankAccountDto) {
    final BankAccount bankAccount = bankAccountMapper.mapToBankAccount(modifiableBankAccountDto);
    if (bankAccount.isActive()) {
      bankAccountRepository.findAll().forEach(b -> b.setActive(false));
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bankAccountRepository.save(bankAccount).getUuid());
  }

  @Override
  public ResponseEntity<BankAccountDto> getBankAccount(final UUID bankAccountId) {
    return ResponseEntity.ok(
        bankAccountRepository
            .findByUuid(bankAccountId)
            .map(bankAccountMapper::mapToBankAccountDto)
            .orElseThrow(() -> NotFoundException.ofBankAccountNotFound(bankAccountId)));
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> changeBankAccount(
      final UUID bankAccountId, final ModifiableBankAccountDto modifiableBankAccountDto) {
    final BankAccount bankAccount =
        bankAccountRepository
            .findByUuid(bankAccountId)
            .orElseThrow(() -> NotFoundException.ofBankAccountNotFound(bankAccountId));

    bankAccountMapper.updateBankAccount(modifiableBankAccountDto, bankAccount);
    if (bankAccount.isActive()) {
      bankAccountRepository.findAll().stream()
          .filter(b -> !b.getUuid().equals(bankAccountId))
          .forEach(b -> b.setActive(false));
    }
    return ResponseEntity.ok(bankAccountRepository.save(bankAccount).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteBankAccount(final UUID bankAccountId) {
    return bankAccountRepository
        .findByUuid(bankAccountId)
        .map(
            bankAccount -> {
              bankAccountRepository.delete(bankAccount);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseThrow(() -> NotFoundException.ofBankAccountNotFound(bankAccountId));
  }
}
