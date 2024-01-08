package com.mav.openzev.mapper;

import com.mav.openzev.api.model.ModifiablePaymentDto;
import com.mav.openzev.api.model.PaymentDto;
import com.mav.openzev.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface PaymentMapper {

  @Mapping(target = "id", source = "uuid")
  @Mapping(target = "invoiceId", source = "invoice.uuid")
  PaymentDto mapToPaymentDto(Payment payment);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "invoice", ignore = true)
  Payment mapToPayment(ModifiablePaymentDto modifiablePaymentDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "invoice", ignore = true)
  void updatePayment(ModifiablePaymentDto modifiablePaymentDto, @MappingTarget Payment payment);
}
