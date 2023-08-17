package com.mav.openzev.mapper;

import com.mav.openzev.api.model.AgreementDto;
import com.mav.openzev.api.model.ModifiableAgreementDto;
import com.mav.openzev.model.Agreement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface AgreementMapper {

  AgreementDto mapToAgreementDto(Agreement agreement);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "accountings", ignore = true)
  Agreement mapToAgreement(ModifiableAgreementDto modifiableAgreementDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "accountings", ignore = true)
  void updateAgreement(
      ModifiableAgreementDto modifiableAgreementDto, @MappingTarget Agreement agreement);
}
