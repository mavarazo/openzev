package com.mav.openzev.controller;

import com.mav.openzev.api.PropertyApi;
import com.mav.openzev.api.model.ModifiablePropertyDto;
import com.mav.openzev.api.model.PropertyDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.PropertyMapper;
import com.mav.openzev.model.Property;
import com.mav.openzev.repository.PropertyRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PropertyController implements PropertyApi {

  private final PropertyRepository propertyRepository;

  private final PropertyMapper propertyMapper;

  @Override
  public ResponseEntity<List<PropertyDto>> getProperties() {
    return ResponseEntity.ok(
        propertyRepository
            .findAll(
                Sort.sort(Property.class)
                    .by(Property::getStreet)
                    .and(Sort.sort(Property.class).by(Property::getHouseNr)))
            .stream()
            .map(propertyMapper::mapToPropertyDto)
            .toList());
  }

  @Override
  public ResponseEntity<PropertyDto> getProperty(final UUID propertyId) {
    return ResponseEntity.ok(
        propertyRepository
            .findByUuid(propertyId)
            .map(propertyMapper::mapToPropertyDto)
            .orElseThrow(() -> NotFoundException.ofPropertyNotFound(propertyId)));
  }

  @Override
  public ResponseEntity<UUID> createProperty(final ModifiablePropertyDto modifiablePropertyDto) {
    final Property property =
        propertyRepository.save(propertyMapper.mapToProperty(modifiablePropertyDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(property.getUuid());
  }

  @Override
  public ResponseEntity<UUID> changeProperty(
      final UUID propertyId, final ModifiablePropertyDto modifiablePropertyDto) {
    return propertyRepository
        .findByUuid(propertyId)
        .map(
            property -> {
              propertyMapper.updateProperty(modifiablePropertyDto, property);
              return ResponseEntity.ok(propertyRepository.save(property).getUuid());
            })
        .orElseThrow(() -> NotFoundException.ofPropertyNotFound(propertyId));
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteProperty(final UUID propertyId) {
    final Property property =
        propertyRepository
            .findByUuid(propertyId)
            .orElseThrow(() -> NotFoundException.ofPropertyNotFound(propertyId));

    if (!property.getUnits().isEmpty()) {
      throw ValidationException.ofPropertyHasUnit(property);
    }

    propertyRepository.delete(property);
    return ResponseEntity.noContent().build();
  }
}
