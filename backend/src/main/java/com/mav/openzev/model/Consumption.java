package com.mav.openzev.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Consumption {

  private OffsetDateTime from;

  private OffsetDateTime upto;

  private Double amountHighTariff;

  private Double amountLowTariff;

  private Double maxPowerBillable;

  private Double maxPowerPhysically;

  private Double quantityHighTariff;

  private Double quantityLowTariff;

  private Double quantityTotal;
}
