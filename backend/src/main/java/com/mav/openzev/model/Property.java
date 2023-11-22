package com.mav.openzev.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "OZEV_PROPERTIES")
public class Property extends AbstractEntity {

  @Column(name = "ACTIVE")
  @Builder.Default
  private boolean active = true;

  @Column(name = "STREET")
  private String street;

  @Column(name = "HOUSE_NR")
  private String houseNr;

  @Column(name = "POSTAL_CODE")
  private String postalCode;

  @Column(name = "CITY")
  private String city;

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Accounting> accountings = new HashSet<>();

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Agreement> agreements = new HashSet<>();

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Owner> owners = new HashSet<>();

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Unit> units = new HashSet<>();

  public Property addAccounting(final Accounting accounting) {
    accountings.add(accounting);
    accounting.setProperty(this);
    return this;
  }

  public Property addAgreement(final Agreement agreement) {
    agreements.add(agreement);
    agreement.setProperty(this);
    return this;
  }

  public Property addOwner(final Owner owner) {
    owners.add(owner);
    owner.setProperty(this);
    return this;
  }

  public Property addUnit(final Unit unit) {
    units.add(unit);
    unit.setProperty(this);
    return this;
  }
}
