package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OZEV_BANK_ACCOUNTS")
public class BankAccount extends AbstractAuditEntity {

  @Column(name = "ACTIVE")
  @Builder.Default
  private boolean active = true;

  @Column(name = "IBAN", nullable = false)
  private String iban;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "STREET")
  private String street;

  @Column(name = "HOUSE_NR")
  private String houseNr;

  @Column(name = "POSTAL_CODE")
  private String postalCode;

  @Column(name = "CITY")
  private String city;
}
