package com.mav.openzev.mapper;

import com.mav.openzev.api.model.BankAccountDto;
import com.mav.openzev.api.model.ModifiableBankAccountDto;
import com.mav.openzev.model.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface BankAccountMapper {

  @Mapping(target = "id", source = "uuid")
  BankAccountDto mapToBankAccountDto(BankAccount bankAccount);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  BankAccount mapToBankAccount(ModifiableBankAccountDto modifiableBankAccountDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  void updateBankAccount(
      ModifiableBankAccountDto modifiableBankAccountDto, @MappingTarget BankAccount bankAccount);
}
