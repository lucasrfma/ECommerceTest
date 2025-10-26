package com.ecommerce.test.shared.dtos;

import java.math.BigDecimal;

public record ProductDto(Long id,
                         String description,
                         String category,
                         BigDecimal price,
                         Integer quantity) {
}
