package com.mav.openzev.mapper;

import com.mav.openzev.api.model.BalanceDto;
import com.mav.openzev.api.model.ModifiableOwnerDto;
import com.mav.openzev.api.model.OwnerDto;
import com.mav.openzev.model.Balance;
import com.mav.openzev.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface OwnerMapper {

  @Mapping(target = "id", source = "uuid")
  OwnerDto mapToOwnerDto(Owner owner);

  BalanceDto mapToBalanceDto(Balance balance);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "invoices", ignore = true)
  @Mapping(target = "ownerships", ignore = true)
  Owner mapToOwner(ModifiableOwnerDto modifiableOwnerDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "invoices", ignore = true)
  @Mapping(target = "ownerships", ignore = true)
  void updateOwner(ModifiableOwnerDto modifiableOwnerDto, @MappingTarget Owner owner);
}
