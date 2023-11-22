package com.mav.openzev.model;

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

@SuperBuilder
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

  @OneToMany(mappedBy = "property")
  @Builder.Default
  private Set<Agreement> agreements = new HashSet<>();

  @OneToMany(mappedBy = "property")
  @Builder.Default
  private Set<Owner> owners = new HashSet<>();

  @OneToMany(mappedBy = "property")
  @Builder.Default
  private Set<Unit> units = new HashSet<>();

  public void addAgreement(final Agreement agreement) {
    agreements.add(agreement);
    agreement.setProperty(this);
  }

  public void addOwner(final Owner owner) {
    owners.add(owner);
    owner.setProperty(this);
  }

  public void addUnit(final Unit unit) {
    units.add(unit);
    unit.setProperty(this);
  }
}
