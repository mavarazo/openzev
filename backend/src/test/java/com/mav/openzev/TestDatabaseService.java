package com.mav.openzev;

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
  public <T> T insert(final T value) {
    entityManager.persist(value);
    return value;
  }
}
