package com.ecommerce.test.productservice.services;

import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.results.ApiResult;
import com.ecommerce.test.productservice.entities.Product;
import com.ecommerce.test.productservice.repositories.ProductRepository;
import com.ecommerce.test.shared.utils.DbUtils;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductService {

    private final ProductRepository productRepository;
    private final Validator validator;

    public ProductService(ProductRepository productRepository, Validator validator) {
        this.productRepository = productRepository;
        this.validator = validator;
    }

    private Product update(Product product, ProductDto productDto) {
        if (productDto.price() != null) {
            product.setPrice(productDto.price());
        }
        if (productDto.category() != null) {
            product.setCategory(productDto.category());
        }
        if (productDto.quantity() != null) {
            product.setStock(product.getStock() + productDto.quantity());
        }

        var violations = validator.validate(product);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return productRepository.save(product);
    }

    private Product insert(ProductDto productDto) {
        return productRepository.save(Product.fromDto(productDto));
    }

    private ProductDto upsertProduct_(ProductDto productDto) {
        Optional<Product> existingProduct = productRepository.findByDescription(productDto.description());
        return existingProduct.map(product ->
                        update(product, productDto)).orElseGet(() -> insert(productDto)
                ).toDto();
    }

    public ApiResult<ProductDto> upsertProduct(ProductDto productDto) {
        return DbUtils.ProtectDbFunction(this::upsertProduct_).apply(productDto);
    }

    public List<ProductDto> getAll() {
        return productRepository.findAll().stream().map(Product::toDto).toList();
    }
}
