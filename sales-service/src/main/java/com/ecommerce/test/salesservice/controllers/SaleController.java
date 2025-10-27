package com.ecommerce.test.salesservice.controllers;

import com.ecommerce.test.salesservice.services.SaleService;
import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.dtos.SaleDto;
import com.ecommerce.test.shared.dtos.SalesRequestDto;
import com.ecommerce.test.shared.results.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@EnableMethodSecurity
@RequestMapping("/api/sales")
public class SaleController {

    final private SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<ApiResult<SaleDto>> RegisterSale(@RequestBody SalesRequestDto salesRequestDto) {
        var result = saleService.saveSale(salesRequestDto);
        return switch (result) {
            case ApiResult.Success<SaleDto> success ->
                    ResponseEntity.ok(success);
            case ApiResult.ValidationFailure<SaleDto> validationFailure ->
                    ResponseEntity.badRequest().body(validationFailure);
            case ApiResult.Failure<SaleDto> failure ->
                    ResponseEntity.internalServerError().body(failure);
        };
    }

    @GetMapping
    public ResponseEntity<List<SaleDto>> GetAllSales() {
        return ResponseEntity.ok(saleService.getAllByAccountId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> GetProductById(@PathVariable("id") Long id) {
        return saleService.getProdById(id);
    }
}
