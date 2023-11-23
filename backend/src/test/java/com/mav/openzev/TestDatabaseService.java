package com.mav.openzev;

import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.model.Document;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.model.Property;
import com.mav.openzev.model.Unit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class TestDatabaseService implements InitializingBean {
  @PersistenceContext private EntityManager entityManager;

  private List<String> tableNames;

  @Transactional
  public void truncateAll() {
    entityManager.flush();
    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

    for (final String tableName : tableNames) {
      entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
    }

    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
  }

  @Override
  public void afterPropertiesSet() {
    tableNames =
        entityManager.getMetamodel().getEntities().stream()
            .filter(e -> e.getJavaType().getAnnotation(Table.class) != null)
            .map(e -> e.getJavaType().getAnnotation(Table.class).name())
            .filter(StringUtils::hasText)
            .collect(Collectors.toList());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Document insertDocument(final Document document) {
    entityManager.persist(document);
    return document;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Property insertProperty(final Property property) {
    entityManager.persist(property);
    return property;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Property updateProperty(final Property property) {
    entityManager.merge(property);
    return property;
  }

  @Deprecated
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void insertAgreement(final Agreement agreement) {
    agreement.getProperty().addAgreement(agreement);
    agreement
        .getAccountings()
        .forEach(
            accounting -> {
              accounting.setAgreement(agreement);
              entityManager.persist(accounting);
            });

    entityManager.persist(agreement.getProperty());
    entityManager.persist(agreement);
  }

  @Deprecated
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Owner insertOwner(final Owner owner) {
    owner.getProperty().addOwner(owner);

    entityManager.persist(owner.getProperty());
    entityManager.persist(owner);
    return owner;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Ownership insertOwnership(final Ownership ownership) {
    entityManager.persist(ownership);
    return ownership;
  }

  @Deprecated
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Unit insertUnit(final Unit unit) {
    unit.getProperty().addUnit(unit);

    entityManager.persist(unit.getProperty());
    entityManager.persist(unit);
    return unit;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void insertAccounting(final Accounting accounting) {
    entityManager.persist(accounting);
  }
}
