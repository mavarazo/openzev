package com.mav.openzev.repository.config;

import com.mav.openzev.model.config.ZevConfig;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZevConfigRepository extends ListCrudRepository<ZevConfig, Long> {

  Optional<ZevConfig> findByUuid(UUID uuid);
}
