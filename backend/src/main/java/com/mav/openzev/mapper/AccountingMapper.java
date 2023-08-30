package com.mav.openzev.mapper;

import static java.util.Objects.nonNull;

import com.mav.openzev.api.model.AccountingDto;
import com.mav.openzev.api.model.ModifiableAccountingDto;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface AccountingMapper {

  @Mapping(target = "agreement", source = "agreement.uuid")
  @Mapping(target = "isDocumentAvailable", source = "document")
  AccountingDto mapToAccountingDto(Accounting accounting);

  default boolean mapToBoolean(final Document document) {
    return nonNull(document) && nonNull(document.getUuid());
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "agreement", ignore = true)
  @Mapping(target = "invoices", ignore = true)
  @Mapping(target = "document", ignore = true)
  Accounting mapToAccounting(ModifiableAccountingDto modifiableAccountingDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "agreement", ignore = true)
  @Mapping(target = "invoices", ignore = true)
  @Mapping(target = "document", ignore = true)
  void updateAccounting(
      ModifiableAccountingDto modifiableAccountingDto, @MappingTarget Accounting accounting);
}
