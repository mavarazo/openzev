package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Settings;
import com.mav.openzev.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingService {

  private final SettingsRepository settingsRepository;

  public Settings findSettingOrFail() {
    return settingsRepository.findAll().stream()
        .findFirst()
        .orElseThrow(NotFoundException::ofSettingsNotFound);
  }
}
