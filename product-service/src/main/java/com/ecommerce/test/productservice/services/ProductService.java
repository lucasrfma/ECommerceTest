package com.ecommerce.test.productservice.services;

import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.dtos.UpsertProductDto;
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

    private Product update(Product product, UpsertProductDto upsertProductDto) {
        if (upsertProductDto.price() != null) {
            product.setPrice(upsertProductDto.price());
        }
        if (upsertProductDto.category() != null) {
            product.setCategory(upsertProductDto.category());
        }
        if (upsertProductDto.quantity() != null) {
            product.setStock(product.getStock() + upsertProductDto.quantity());
        }

        var violations = validator.validate(product);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return productRepository.save(product);
    }

    private Product insert(UpsertProductDto upsertProductDto) {
        return productRepository.save(Product.fromUpsertDto(upsertProductDto));
    }

    private ProductDto upsertProduct_(UpsertProductDto upsertProductDto) {
        Optional<Product> existingProduct = productRepository.findByDescription(upsertProductDto.description());
        return existingProduct.map(product ->
                        update(product, upsertProductDto)).orElseGet(() -> insert(upsertProductDto)
                ).toDto();
    }

    public ApiResult<ProductDto> upsertProduct(UpsertProductDto upsertProductDto) {
        return DbUtils.ProtectDbFunction(this::upsertProduct_).apply(upsertProductDto);
    }

    public List<ProductDto> getAll() {
        return productRepository.findAll().stream().map(Product::toDto).toList();
    }
}
