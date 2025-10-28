package com.ecommerce.test.salesservice.controllers;

import com.ecommerce.test.salesservice.services.SaleService;
import com.ecommerce.test.shared.dtos.SaleDto;
import com.ecommerce.test.shared.dtos.SalesRequestDto;
import com.ecommerce.test.shared.results.ApiResult;
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
@RequestMapping("/api/sales")
public class SaleController {

    final private SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda feita com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Produto não encontrado ou estoque insuficiente",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Pedido requer autenticação", content = @Content)
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vendas obtida com sucesso",
                    content = @Content),
    })
    public ResponseEntity<List<SaleDto>> GetAllSales() {
        return ResponseEntity.ok(saleService.getAllByAccountId());
    }
}
