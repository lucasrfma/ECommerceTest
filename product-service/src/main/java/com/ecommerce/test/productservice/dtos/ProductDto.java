package com.ecommerce.test.productservice.dtos;

import java.math.BigDecimal;

public record ProductDto(String description,
                         String category,
                         BigDecimal price,
                         Integer quantity) {
}
