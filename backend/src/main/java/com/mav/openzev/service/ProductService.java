package com.mav.openzev.service;

import static java.util.Objects.isNull;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Product;
import com.mav.openzev.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public Optional<Product> findProduct(final UUID productId) {
    return isNull(productId)
        ? Optional.empty()
        : Optional.ofNullable(
            productRepository
                .findByUuid(productId)
                .orElseThrow(() -> NotFoundException.ofProductNotFound(productId)));
  }
}
