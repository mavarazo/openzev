package com.mav.openzev.controller;

import com.mav.openzev.api.SettingApi;
import com.mav.openzev.api.model.ModifiableSettingsDto;
import com.mav.openzev.api.model.SettingsDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.SettingsMapper;
import com.mav.openzev.model.Settings;
import com.mav.openzev.repository.SettingsRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SettingController implements SettingApi {

  private final SettingsRepository settingsRepository;
  private final SettingsMapper settingsMapper;

  @Override
  public ResponseEntity<SettingsDto> getSettings() {
    return ResponseEntity.ok(
        settingsRepository.findAll().stream()
            .findFirst()
            .map(settingsMapper::mapToSettingsDto)
            .orElseThrow(NotFoundException::ofSettingsNotFound));
  }

  @Override
  public ResponseEntity<UUID> saveSettings(final ModifiableSettingsDto modifiableSettingsDto) {
    final Settings settings =
        settingsRepository.findAll().stream()
            .findFirst()
            .map(
                z -> {
                  settingsMapper.updateSettings(modifiableSettingsDto, z);
                  return z;
                })
            .orElse(settingsMapper.mapToSettings(modifiableSettingsDto));

    return ResponseEntity.status(HttpStatus.OK).body(settingsRepository.save(settings).getUuid());
  }
}
