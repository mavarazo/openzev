package com.mav.openzev.mapper;

import com.mav.openzev.api.model.InvoiceDto;
import com.mav.openzev.api.model.ModifiableInvoiceDto;
import com.mav.openzev.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface InvoiceMapper {

  @Mapping(target = "id", source = "uuid")
  @Mapping(target = "unitId", source = "unit.uuid")
  @Mapping(target = "recipientId", source = "recipient.uuid")
  InvoiceDto mapToInvoiceDto(Invoice invoice);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "unit", ignore = true)
  @Mapping(target = "recipient", ignore = true)
  @Mapping(target = "type", constant = "STANDARD")
  @Mapping(target = "items", ignore = true)
  @Mapping(target = "payments", ignore = true)
  @Mapping(target = "documents", ignore = true)
  Invoice mapToInvoice(ModifiableInvoiceDto modifiableInvoiceDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "unit", ignore = true)
  @Mapping(target = "recipient", ignore = true)
  @Mapping(target = "type", constant = "STANDARD")
  @Mapping(target = "items", ignore = true)
  @Mapping(target = "payments", ignore = true)
  @Mapping(target = "documents", ignore = true)
  void updateInvoice(ModifiableInvoiceDto modifiableInvoiceDto, @MappingTarget Invoice invoice);
}
