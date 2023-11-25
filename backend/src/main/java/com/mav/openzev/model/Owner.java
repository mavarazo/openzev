package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "OZEV_OWNERS")
public class Owner extends AbstractEntity {

  @Column(name = "ACTIVE")
  @Builder.Default
  private boolean active = true;

  @Column(name = "CONTRACT_ID")
  private String contractId;

  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "STREET")
  private String street;

  @Column(name = "HOUSE_NR")
  private String houseNr;

  @Column(name = "POSTAL_CODE")
  private String postalCode;

  @Column(name = "CITY")
  private String city;

  @Column(name = "PHONE_NR")
  private String phoneNr;

  @Column(name = "MOBILE_NR")
  private String mobileNr;

  @OneToMany(mappedBy = "owner")
  private Set<Ownership> ownerships;

  public Owner addOwnership(final Ownership ownership) {
    ownerships.add(ownership);
    ownership.setOwner(this);
    return this;
  }
}
