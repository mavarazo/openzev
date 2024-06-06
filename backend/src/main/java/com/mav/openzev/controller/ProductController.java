package com.mav.openzev.controller;

import com.mav.openzev.api.ProductApi;
import com.mav.openzev.api.model.ModifiableProductDto;
import com.mav.openzev.api.model.ProductDto;
import com.mav.openzev.mapper.ProductMapper;
import com.mav.openzev.model.Product;
import com.mav.openzev.repository.ProductRepository;
import com.mav.openzev.service.ProductService;
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
  private final ProductService productService;
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
    final Product product = productService.findProductOrFail(productId);
    return ResponseEntity.ok(productMapper.mapToProductDto(product));
  }

  @Override
  public ResponseEntity<UUID> changeProduct(
      final UUID productId, final ModifiableProductDto modifiableProductDto) {
    final Product product = productService.findProductOrFail(productId);
    productMapper.updateProduct(modifiableProductDto, product);
    return ResponseEntity.ok(productRepository.save(product).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteProduct(final UUID productId) {
    productService.deleteProduct(productId);
    return ResponseEntity.noContent().build();
  }
}
