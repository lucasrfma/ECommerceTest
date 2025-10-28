package com.ecommerce.test.salesservice.services;

import com.ecommerce.test.salesservice.entities.Sale;
import com.ecommerce.test.salesservice.feign.ProductClient;
import com.ecommerce.test.salesservice.repositores.SaleRepository;
import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.dtos.SaleDto;
import com.ecommerce.test.shared.dtos.SalesRequestDto;
import com.ecommerce.test.shared.dtos.UpsertProductDto;
import com.ecommerce.test.shared.results.ApiResult;
import com.ecommerce.test.shared.utils.MiscUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private SaleService saleService;

    // Mocks
    static final private MiscUtils.Principal principal = new MiscUtils.Principal(1L, "test@test.com");

    static final private Long productId = 1L;
    static final private ProductDto productDto = new ProductDto(productId,
            "Test Product",
            "Description",
            BigDecimal.TEN.setScale(2, RoundingMode.HALF_DOWN),
            10);

    private void setSuccessfulUpdateProductMock() {
        when(productClient.RegisterProduct(any(UpsertProductDto.class))).thenAnswer(invocation -> {
            UpsertProductDto arg = invocation.getArgument(0);
            ProductDto updatedProduct =  new ProductDto(
                    productDto.id(),
                    productDto.description(),
                    productDto.category(),
                    productDto.price(),
                    productDto.quantity() + arg.quantity()
            );
            return ResponseEntity.ok(new ApiResult.Success<>(updatedProduct));
        });
    }

    private void setSuccessfulSaveSaleMock() {
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale arg = invocation.getArgument(0);
            new Sale(
                    arg.getProductId(),
                    arg.getProductDescription(),
                    arg.getQuantity(),
                    arg.getPrice(),
                    arg.getAccountId(),
                    arg.getAccountEmail()
            );
            arg.setId(1L);
            return arg;
        });
    }

    @Test
    void testSaveSale_Success() {
        // Arrange
        // pedido recebido
        SalesRequestDto salesRequestDto = new SalesRequestDto(1L, 5);
        // resposta a mockar do query de produto por ID
        ResponseEntity<ProductDto> productResponse = ResponseEntity.ok(productDto);

        try (MockedStatic<MiscUtils> mockedStatic = Mockito.mockStatic(MiscUtils.class)) {
            // Configurando mocks para devolver as respostas acima
            mockedStatic.when(MiscUtils::getCurrentPrincipal).thenReturn(principal);
            when(productClient.GetProductById(salesRequestDto.productId())).thenReturn(productResponse);
            // Mockando com thenAnswer para que os mocks simulem o comportamento dos métodos mockados,
            // e não simplesmente nos dêem a resposta desejada de maneira hard-coded
            setSuccessfulUpdateProductMock();
            setSuccessfulSaveSaleMock();

            // Resposta esperada
            SaleDto expectedSale = new SaleDto(
                    1L,
                    productDto.id(),
                    productDto.description(),
                    productDto.quantity() - salesRequestDto.quantity(),
                    productDto.price(),
                    principal.email(),
                    LocalDateTime.now()
            );

            // Act
            ApiResult<SaleDto> result = saleService.saveSale(salesRequestDto);

            // Assert
            assertInstanceOf(ApiResult.Success.class, result);
            ApiResult.Success<SaleDto> successResult = (ApiResult.Success<SaleDto>) result;
            // Simplificando pra igualar só quantity resultante, pois é onde a chance de erro está mais
            // e os saleDates seriam diferentes nos milisegundos.
            assertEquals(expectedSale.quantity(), successResult.getSuccessData().quantity());
        }
    }

    @Test
    void testSaveSale_ProductNotFound() {
        SalesRequestDto salesRequestDto = new SalesRequestDto(1L, 5);
        ResponseEntity<ProductDto> productResponse = ResponseEntity.notFound().build();

        when(productClient.GetProductById(salesRequestDto.productId())).thenReturn(productResponse);

        ApiResult<SaleDto> result = saleService.saveSale(salesRequestDto);

        assertInstanceOf(ApiResult.ValidationFailure.class, result);
        ApiResult.ValidationFailure<SaleDto> validationFailure = (ApiResult.ValidationFailure<SaleDto>) result;
        assertEquals("Produto não encontrado.", validationFailure.getErrorMessage());
    }

    @Test
    void testSaveSale_InsufficientStock() {
        SalesRequestDto salesRequestDto = new SalesRequestDto(1L, 15);
        ResponseEntity<ProductDto> productResponse = ResponseEntity.ok(productDto);

        when(productClient.GetProductById(salesRequestDto.productId())).thenReturn(productResponse);

        ApiResult<SaleDto> result = saleService.saveSale(salesRequestDto);

        assertInstanceOf(ApiResult.ValidationFailure.class, result);
        ApiResult.ValidationFailure<SaleDto> validationFailure = (ApiResult.ValidationFailure<SaleDto>) result;
        assertEquals("Não há estoque suficiente.", validationFailure.getErrorMessage());
    }

    @Test
    void testGetAllByAccountId() {
        Sale sale = new Sale(1L, "Test Product", 5, BigDecimal.TEN, 1L, "test@test.com");
        List<Sale> sales = Collections.singletonList(sale);

        try (MockedStatic<MiscUtils> mockedStatic = Mockito.mockStatic(MiscUtils.class)) {
            mockedStatic.when(MiscUtils::getCurrentPrincipal).thenReturn(principal);
            when(saleRepository.findAllByAccountId(principal.id())).thenReturn(sales);

            List<SaleDto> result = saleService.getAllByAccountId();

            assertEquals(1, result.size());
            assertEquals(sale.toDto(), result.getFirst());
        }
    }
}
