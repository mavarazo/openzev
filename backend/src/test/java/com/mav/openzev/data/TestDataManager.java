package com.mav.openzev.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TestDataManager implements InitializingBean {

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
    entityManager.createNativeQuery("SET session_replication_role = 'replica';").executeUpdate();

    for (final String tableName : tableNames) {
      entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " CASCADE;").executeUpdate();
    }

    entityManager.createNativeQuery("SET session_replication_role = 'origin';").executeUpdate();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public <T> T persist(final T value) {
    entityManager.persist(value);
    return value;
  }
}
