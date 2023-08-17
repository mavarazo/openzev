package com.mav.openzev;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
  public void afterPropertiesSet() throws Exception {
    tableNames =
        entityManager.getMetamodel().getEntities().stream()
            .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
            .map(e -> e.getJavaType().getAnnotation(Entity.class).name())
            .peek(msg -> log.info(">>> " + msg))
            .collect(Collectors.toList());
  }
}
