package com.mav.openzev.repository;

import com.mav.openzev.model.Settings;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends ListCrudRepository<Settings, Long> {

  Optional<Settings> findByUuid(UUID uuid);
}
