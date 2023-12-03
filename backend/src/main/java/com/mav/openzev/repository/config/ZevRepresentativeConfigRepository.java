package com.mav.openzev.repository.config;

import com.mav.openzev.model.config.ZevRepresentativeConfig;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZevRepresentativeConfigRepository
    extends ListCrudRepository<ZevRepresentativeConfig, Long> {

  Optional<ZevRepresentativeConfig> findByUuid(UUID uuid);
}
