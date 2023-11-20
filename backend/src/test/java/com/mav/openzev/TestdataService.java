package com.mav.openzev;

import com.mav.openzev.model.Agreement;
import com.mav.openzev.model.Property;
import com.mav.openzev.repository.AgreementRepository;
import com.mav.openzev.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TestdataService {

  private final PropertyRepository propertyRepository;
  private final AgreementRepository agreementRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void insertAgreement(final Agreement agreement, final Property property) {
    property.addAgreement(agreement);
    propertyRepository.save(property);
    agreementRepository.save(agreement);
  }
}
