package com.mav.openzev.adapter.qrgenerator.model.generator;

import com.mav.openzev.model.BankAccountModels;
import com.mav.openzev.model.OwnerModels;
import com.mav.openzev.model.SettingsModels;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestModels {

  public static Request get() {
    return new Request(
        buildCreditorRequest(), buildDebitorRequest(), buildFormatRequest(), buildPaymentRequest());
  }

  private static Creditor buildCreditorRequest() {
    return Creditor.builder()
        .account(BankAccountModels.getBankAccount().getIban())
        .name(SettingsModels.ZEV_STRADUN_30_GRATTAVACHE)
        .street(SettingsModels.STRADUN)
        .houseNo(SettingsModels._30)
        .postalCode(SettingsModels._1624)
        .town(SettingsModels.GRATTAVACHE)
        .build();
  }

  private static Debitor buildDebitorRequest() {
    return Debitor.builder()
        .name(
            "%s %s"
                .formatted(
                    OwnerModels.getOwner().getFirstName(), OwnerModels.getOwner().getLastName()))
        .street(OwnerModels.getOwner().getStreet())
        .houseNo(OwnerModels.getOwner().getHouseNr())
        .postalCode(OwnerModels.getOwner().getPostalCode())
        .town(OwnerModels.getOwner().getCity())
        .build();
  }

  private static Format buildFormatRequest() {
    return Format.builder()
        .outputSize(Format.OutputSize.A4_PORTRAIT_SHEET)
        .graphicsFormat(Format.GraphicsFormat.PDF)
        .build();
  }

  private static Payment buildPaymentRequest() {
    return Payment.builder()
        .amount(123.35)
        .unstructuredMessage("649b2096-4976-4a09-b88d-7dd8c5d0315a")
        .build();
  }
}
