package com.ecommerce.test.productservice.controllers;

import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.dtos.UpsertProductDto;
import com.ecommerce.test.shared.results.ApiResult;
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

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto registrado ou atualizado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Produto com parâmetros inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Pedido requer autenticação", content = @Content)
    })
    public ResponseEntity<ApiResult<ProductDto>> RegisterProduct(@RequestBody UpsertProductDto upsertProductDto) {
        ApiResult<ProductDto> result = productService.upsertProduct(upsertProductDto);
        return switch (result) {
            case ApiResult.Success<ProductDto> success ->
                    ResponseEntity.ok(success);
            case ApiResult.ValidationFailure<ProductDto> validationFailure ->
                    ResponseEntity.badRequest().body(validationFailure);
            case ApiResult.Failure<ProductDto> failure ->
                    ResponseEntity.internalServerError().body(failure);
        };
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos obtida com sucesso", content = @Content),
    })
    public List<ProductDto> GetAllProducts() {
        return productService.getAll();
    }
}
