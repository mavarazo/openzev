package com.mav.openzev;

import com.mav.openzev.model.Document;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Item;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.model.Payment;
import com.mav.openzev.model.Product;
import com.mav.openzev.model.Unit;
import com.mav.openzev.model.config.ZevConfig;
import com.mav.openzev.model.config.ZevRepresentativeConfig;
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

  @Override
  public void afterPropertiesSet() {
    tableNames =
        entityManager.getMetamodel().getEntities().stream()
            .filter(e -> e.getJavaType().getAnnotation(Table.class) != null)
            .map(e -> e.getJavaType().getAnnotation(Table.class).name())
            .filter(StringUtils::hasText)
            .collect(Collectors.toList());
  }

  @Transactional
  public void truncateAll() {
    entityManager.flush();
    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

    for (final String tableName : tableNames) {
      entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
    }

    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Document insert(final Document document) {
    entityManager.persist(document);
    return document;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Invoice insert(final Invoice invoice) {
    entityManager.persist(invoice);
    return invoice;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Item insert(final Item item) {
    entityManager.persist(item);
    return item;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Owner insert(final Owner owner) {
    entityManager.persist(owner);
    return owner;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Ownership insert(final Ownership ownership) {
    entityManager.persist(ownership);
    return ownership;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Product insert(final Product product) {
    entityManager.persist(product);
    return product;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Unit insert(final Unit unit) {
    entityManager.persist(unit);
    return unit;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ZevConfig insert(final ZevConfig zevConfig) {
    entityManager.persist(zevConfig);
    return zevConfig;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ZevRepresentativeConfig insert(final ZevRepresentativeConfig zevRepresentativeConfig) {
    entityManager.persist(zevRepresentativeConfig);
    return zevRepresentativeConfig;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Payment insert(final Payment payment) {
    entityManager.persist(payment);
    return payment;
  }
}
