package com.ecommerce.test.salesservice.services;

import com.ecommerce.test.salesservice.entities.Sale;
import com.ecommerce.test.salesservice.feign.ProductClient;
import com.ecommerce.test.salesservice.repositores.SaleRepository;
import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.dtos.SaleDto;
import com.ecommerce.test.shared.dtos.SalesRequestDto;
import com.ecommerce.test.shared.dtos.UpsertProductDto;
import com.ecommerce.test.shared.exceptions.ManualValidationException;
import com.ecommerce.test.shared.results.ApiResult;
import com.ecommerce.test.shared.utils.DbUtils;
import com.ecommerce.test.shared.utils.MiscUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductClient productClient;

    public SaleService(SaleRepository saleRepository, ProductClient productClient) {
        this.saleRepository = saleRepository;
        this.productClient = productClient;
    }

    public ResponseEntity<ProductDto> getProdById(Long id) {
        return productClient.GetProductById(id);
    }

    private static UpsertProductDto createUpdateRequestBody(
            Integer quantity, ResponseEntity<ProductDto> productResponse)
            throws ManualValidationException, NullPointerException {
        if (!productResponse.getStatusCode().is2xxSuccessful()) {
            throw new ManualValidationException("Produto não encontrado.");
        }

        ProductDto retrievedProduct = productResponse.getBody();
        if (retrievedProduct.quantity() < quantity) {
            throw new ManualValidationException("Não há estoque suficiente.");
        }

        return new UpsertProductDto(
                retrievedProduct.description(),
                null,
                null,
                quantity *-1
        );
    }

    private SaleDto saveSale_(SalesRequestDto salesRequestDto)
            throws ManualValidationException,
            NullPointerException,
            NumberFormatException,
            ArrayIndexOutOfBoundsException {
        
        var productResponse = productClient.GetProductById(salesRequestDto.productId());

        var updateRequest = createUpdateRequestBody(salesRequestDto.quantity(), productResponse);

        var productUpdateResponse = productClient.RegisterProduct(updateRequest);

        if (!productUpdateResponse.getStatusCode().is2xxSuccessful()) {
            var response = productUpdateResponse.getBody();
            throw new ManualValidationException(response.getErrorMessage());
        }

        var updated = productUpdateResponse.getBody().getSuccessData();

        MiscUtils.Principal principal = MiscUtils.getCurrentPrincipal();

        return saleRepository.save(new Sale(
                updated.id(),
                updated.description(),
                salesRequestDto.quantity(),
                updated.price(),
                principal.id(),
                principal.email()
        )).toDto();
    }

    public ApiResult<SaleDto> saveSale(SalesRequestDto salesRequestDto) {
        return DbUtils.ProtectDbFunction(this::saveSale_).apply(salesRequestDto);
    }

    public List<SaleDto> getAllByAccountId() {
        return saleRepository.findAllByAccountId(
                MiscUtils.getCurrentPrincipal().id()
        ).stream().map(Sale::toDto).toList();
    }
}
