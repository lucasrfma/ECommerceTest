package com.ecommerce.test.productservice.services;

import com.ecommerce.test.productservice.entities.Product;
import com.ecommerce.test.productservice.repositories.ProductRepository;
import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.dtos.UpsertProductDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private static Validator validator;

    private ProductService productService;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        // Manually inject mocks into AccountService constructor
        productService = new ProductService(
                productRepository, // jwtSecret
                validator // jwtDuration
        );
    }

    private static final UpsertProductDto upsertDto = new UpsertProductDto(
            "Product", "Category",
            BigDecimal.TEN.setScale(2, RoundingMode.HALF_DOWN), 10);

    @Test
    void testUpsertProduct_SuccessfulInsert() {
        when(productRepository.findByDescription("Product")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = productService.upsertProduct(upsertDto);

        assertTrue(result.isSuccess());
        var returnedDto = result.getSuccessData();
        assertEquals(returnedDto.description(), upsertDto.description());
        assertEquals(returnedDto.category(), upsertDto.category());
        assertEquals(returnedDto.price(), upsertDto.price());
        assertEquals(returnedDto.quantity(), upsertDto.quantity());
    }

    @Test
    void testUpsertProduct_SuccessfulUpdate() {
        Product productCopy = Product.fromUpsertDto(upsertDto);
        Product product = Product.fromUpsertDto(upsertDto);
        product.setId(1L);
        when(productRepository.findByDescription("Product")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = productService.upsertProduct(upsertDto);

        assertTrue(result.isSuccess());
        var returnedDto = result.getSuccessData();
        assertEquals(returnedDto.description(), upsertDto.description());
        assertEquals(returnedDto.category(), upsertDto.category());
        assertEquals(returnedDto.price(), upsertDto.price());
        assertEquals(returnedDto.quantity(), upsertDto.quantity() + productCopy.getStock());
    }

    @Test
    void testUpsertProduct_ValidationException() {

        UpsertProductDto badUpsertDto = new UpsertProductDto("Product",null,
                BigDecimal.valueOf(10.005).setScale(3, RoundingMode.HALF_DOWN), null);
        Product product = Product.fromUpsertDto(upsertDto);
        product.setId(1L);
        when(productRepository.findByDescription("Product")).thenReturn(Optional.of(product));

        var result = productService.upsertProduct(badUpsertDto);

        assertFalse(result.isSuccess());
        String errorMsg = result.getErrorMessage();
        assertEquals("price: Formato de preço inválido.", errorMsg);
    }

    @Test
    void testGetAll() {
        when(productRepository.findAll()).thenReturn(List.of(Product.fromUpsertDto(upsertDto)));
        List<ProductDto> result = productService.getAll();
        assertEquals(1, result.size());
        assertEquals(upsertDto.description(), result.getFirst().description());
        assertEquals(upsertDto.category(), result.getFirst().category());
        assertEquals(upsertDto.price(), result.getFirst().price());
        assertEquals(upsertDto.quantity(), result.getFirst().quantity());
    }

    @Test
    void testGetAll_EmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        List<ProductDto> result = productService.getAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetById_Success() {
        Product product = Product.fromUpsertDto(upsertDto);
        Long id = 1L;
        product.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        var result = productService.getById(id);

        assertTrue(result.isSuccess());
        var returnedDto = result.getSuccessData();
        assertEquals(returnedDto.description(), upsertDto.description());
        assertEquals(returnedDto.category(), upsertDto.category());
        assertEquals(returnedDto.price(), upsertDto.price());
        assertEquals(returnedDto.quantity(), upsertDto.quantity());
    }

    @Test
    void testGetById_NotFound() {
        Long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        var result = productService.getById(id);

        assertFalse(result.isSuccess());
        var errorMsg = result.getErrorMessage();
        assertEquals(ProductService.PRODUCT_NOT_FOUND, errorMsg);
    }
}
