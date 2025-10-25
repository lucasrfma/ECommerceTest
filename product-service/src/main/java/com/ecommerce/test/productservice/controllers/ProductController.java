package com.ecommerce.test.productservice.controllers;

import com.ecommerce.test.productservice.dtos.ProductDto;
import com.ecommerce.test.productservice.repositories.ProductRepository;
import com.ecommerce.test.productservice.results.ApiResult;
import com.ecommerce.test.productservice.services.ProductService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@EnableMethodSecurity
@RequestMapping("/api/products")
public class ProductController {

    final private ProductService productService;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta registrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "Conta já existe, ou parâmetros inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content)
    })
    public ResponseEntity<ApiResult<ProductDto>> RegisterProduct(@RequestBody ProductDto productDto) {
        ApiResult<ProductDto> result = productService.upsertProduct(productDto);
        return switch (result) {
            case ApiResult.Success<ProductDto> success ->
                    ResponseEntity.ok(success);
            case ApiResult.Failure<ProductDto> failure ->
                    ResponseEntity.badRequest().body(failure);
        };
    }

    @GetMapping
    public List<ProductDto> GetAllProducts() {
        return productService.getAll();
    }
}
