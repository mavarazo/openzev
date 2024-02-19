package com.mav.openzev.controller;

import com.mav.openzev.api.ProductApi;
import com.mav.openzev.api.model.ModifiableProductDto;
import com.mav.openzev.api.model.ProductDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.ProductMapper;
import com.mav.openzev.model.Product;
import com.mav.openzev.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductApi {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Override
  public ResponseEntity<List<ProductDto>> getProducts() {
    return ResponseEntity.ok(
        productRepository
            .findAll(Sort.sort(Product.class).by(Product::getSubject).ascending())
            .stream()
            .map(productMapper::mapToProductDto)
            .toList());
  }

  @Override
  public ResponseEntity<UUID> createProduct(final ModifiableProductDto modifiableProductDto) {
    final Product product = productMapper.mapToProduct(modifiableProductDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(productRepository.save(product).getUuid());
  }

  @Override
  public ResponseEntity<ProductDto> getProduct(final UUID productId) {
    return ResponseEntity.ok(
        productRepository
            .findByUuid(productId)
            .map(productMapper::mapToProductDto)
            .orElseThrow(() -> NotFoundException.ofProductNotFound(productId)));
  }

  @Override
  public ResponseEntity<UUID> changeProduct(
      final UUID productId, final ModifiableProductDto modifiableProductDto) {
    final Product product =
        productRepository
            .findByUuid(productId)
            .orElseThrow(() -> NotFoundException.ofProductNotFound(productId));

    productMapper.updateProduct(modifiableProductDto, product);
    return ResponseEntity.ok(productRepository.save(product).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteProduct(final UUID productId) {
    return productRepository
        .findByUuid(productId)
        .map(
            product -> {
              productRepository.delete(product);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseThrow(() -> NotFoundException.ofProductNotFound(productId));
  }
}
