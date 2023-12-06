package com.mav.openzev.controller;

import com.mav.openzev.api.ConfigApi;
import com.mav.openzev.api.model.ModifiableZevConfigDto;
import com.mav.openzev.api.model.ModifiableZevRepresentativeConfigDto;
import com.mav.openzev.api.model.ZevConfigDto;
import com.mav.openzev.api.model.ZevRepresentativeConfigDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.ZevConfigMapper;
import com.mav.openzev.mapper.ZevRepresentativeConfigMapper;
import com.mav.openzev.model.config.ZevConfig;
import com.mav.openzev.model.config.ZevRepresentativeConfig;
import com.mav.openzev.repository.config.ZevConfigRepository;
import com.mav.openzev.repository.config.ZevRepresentativeConfigRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConfigController implements ConfigApi {

  private final ZevConfigRepository zevConfigRepository;
  private final ZevConfigMapper zevConfigMapper;

  private final ZevRepresentativeConfigRepository zevRepresentativeConfigRepository;
  private final ZevRepresentativeConfigMapper zevRepresentativeConfigMapper;

  @Override
  public ResponseEntity<ZevConfigDto> getZevConfig() {
    return ResponseEntity.ok(
        zevConfigRepository.findAll().stream()
            .findFirst()
            .map(zevConfigMapper::mapToZevConfigDto)
            .orElseThrow(NotFoundException::ofZevConfigNotFound));
  }

  @Override
  public ResponseEntity<UUID> saveZevConfig(final ModifiableZevConfigDto modifiableZevConfigDto) {
    final ZevConfig zevConfig =
        zevConfigRepository.findAll().stream()
            .findFirst()
            .map(
                z -> {
                  zevConfigMapper.updateZevConfig(modifiableZevConfigDto, z);
                  return z;
                })
            .orElse(zevConfigMapper.mapToZevConfig(modifiableZevConfigDto));

    return ResponseEntity.status(HttpStatus.OK).body(zevConfigRepository.save(zevConfig).getUuid());
  }

  @Override
  public ResponseEntity<ZevRepresentativeConfigDto> getZevRepresentativeConfig() {
    return ResponseEntity.ok(
        zevRepresentativeConfigRepository.findAll().stream()
            .findFirst()
            .map(zevRepresentativeConfigMapper::mapToZevRepresentativeConfigDto)
            .orElseThrow(NotFoundException::ofZevRepresentativeConfigNotFound));
  }

  @Override
  public ResponseEntity<UUID> saveZevRepresentativeConfig(
      final ModifiableZevRepresentativeConfigDto modifiableZevRepresentativeConfigDto) {
    final ZevRepresentativeConfig zevRepresentativeConfig =
        zevRepresentativeConfigRepository.findAll().stream()
            .findFirst()
            .map(
                z -> {
                  zevRepresentativeConfigMapper.updateZevRepresentativeConfig(
                      modifiableZevRepresentativeConfigDto, z);
                  return z;
                })
            .orElse(
                zevRepresentativeConfigMapper.mapToZevRepresentativeConfig(
                    modifiableZevRepresentativeConfigDto));

    return ResponseEntity.status(HttpStatus.OK)
        .body(zevRepresentativeConfigRepository.save(zevRepresentativeConfig).getUuid());
  }
}
