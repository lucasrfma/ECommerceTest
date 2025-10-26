package com.ecommerce.test.shared.dtos;

import java.math.BigDecimal;

public record ProductDto(String description,
                         String category,
                         BigDecimal price,
                         Integer quantity) {
}
