package com.mav.openzev.controller;

import com.mav.openzev.api.BankAccountApi;
import com.mav.openzev.api.model.BankAccountDto;
import com.mav.openzev.api.model.ModifiableBankAccountDto;
import com.mav.openzev.mapper.BankAccountMapper;
import com.mav.openzev.model.BankAccount;
import com.mav.openzev.repository.BankAccountRepository;
import com.mav.openzev.service.BankAccountService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BankAccountController implements BankAccountApi {

  private final BankAccountRepository bankAccountRepository;
  private final BankAccountService bankAccountService;
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
  public ResponseEntity<UUID> createBankAccount(
      final ModifiableBankAccountDto modifiableBankAccountDto) {
    final BankAccount bankAccount = bankAccountMapper.mapToBankAccount(modifiableBankAccountDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bankAccountService.saveBankAccount(bankAccount).getUuid());
  }

  @Override
  public ResponseEntity<BankAccountDto> getBankAccount(final UUID bankAccountId) {
    final BankAccount bankAccount = bankAccountService.findBankAccountOrFail(bankAccountId);
    return ResponseEntity.ok(bankAccountMapper.mapToBankAccountDto(bankAccount));
  }

  @Override
  public ResponseEntity<UUID> changeBankAccount(
      final UUID bankAccountId, final ModifiableBankAccountDto modifiableBankAccountDto) {
    final BankAccount bankAccount = bankAccountService.findBankAccountOrFail(bankAccountId);
    bankAccountMapper.updateBankAccount(modifiableBankAccountDto, bankAccount);
    return ResponseEntity.ok(bankAccountService.saveBankAccount(bankAccount).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteBankAccount(final UUID bankAccountId) {
    bankAccountRepository.delete(bankAccountService.findBankAccountOrFail(bankAccountId));
    return ResponseEntity.noContent().build();
  }
}
