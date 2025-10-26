package com.ecommerce.test.salesservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Sale {

    static final int MAX_DESCRIPTION_LEN = 100;
    static final int PRICE_PRECISION = 10;
    static final int PRICE_SCALE = 2;

    static final int MAX_EMAIL_LEN = 100;

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    @Column(length = MAX_DESCRIPTION_LEN)
    @Size(max = MAX_DESCRIPTION_LEN, message = "A descrição deve ter no máximo " + MAX_DESCRIPTION_LEN + " caracteres")
    private String productDescription;
    @Column(nullable = false)
    @Min(value = 0, message = "A quantidade vendida deve ser maior ou igual a zero")
    private Integer quantity;
    @Column(nullable = false, precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal price;

    private Long accountId;
    @Column(length = MAX_EMAIL_LEN)
    private String accountEmail;

    private LocalDateTime saleDate;
}
