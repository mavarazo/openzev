package com.mav.openzev.adapter.qrgenerator.model.generator;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Payment {
  @NonNull @Builder.Default private Currency currency = Currency.CHF;
  @NonNull private Double amount;
  private String reference;
  private String unstructuredMessage;
  private String referenceInput;

  public enum Currency {
    CHF,
    EUR
  }
}
