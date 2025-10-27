package com.ecommerce.test.shared.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleDto(
        Long id,
        Long productId,
        String productDescription,
        Integer quantity,
        BigDecimal price,
        String accountEmail,
        LocalDateTime saleDate) {
}
