package com.mav.openzev.service;

import static java.util.Objects.isNull;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.model.Product;
import com.mav.openzev.repository.ItemRepository;
import com.mav.openzev.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ItemRepository itemRepository;

  public Product findProductOrFail(final UUID productId) {
    return productRepository
        .findByUuid(productId)
        .orElseThrow(() -> NotFoundException.ofProductNotFound(productId));
  }

  public Optional<Product> findProduct(final UUID productId) {
    return isNull(productId) ? Optional.empty() : Optional.ofNullable(findProductOrFail(productId));
  }

  public void deleteProduct(final UUID productId) {
    final Product product = findProductOrFail(productId);
    if (itemRepository.existsByProductUuid(productId)) {
      throw ValidationException.ofProductIsUsed(product);
    }
    productRepository.delete(product);
  }
}
