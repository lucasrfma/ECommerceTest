package com.ecommerce.test.shared.dtos;

import java.math.BigDecimal;

public record UpsertProductDto(String description,
                               String category,
                               BigDecimal price,
                               Integer quantity) {
}
