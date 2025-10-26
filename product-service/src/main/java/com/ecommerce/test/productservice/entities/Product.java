package com.ecommerce.test.productservice.entities;

import com.ecommerce.test.shared.dtos.ProductDto;
import com.ecommerce.test.shared.dtos.UpsertProductDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Product {

    static final int MAX_DESCRIPTION_LEN = 100;
    static final int MAX_CATEGORY_LEN = 60;
    static final int PRICE_PRECISION = 10;
    static final int PRICE_SCALE = 2;


    public Product(String description, String category, BigDecimal price, Integer stock) {
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = MAX_DESCRIPTION_LEN)
    @Size(max = MAX_DESCRIPTION_LEN, message = "A descrição deve ter no máximo " + MAX_DESCRIPTION_LEN + " caracteres")
    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @Column(nullable = false, length = MAX_CATEGORY_LEN)
    @Size(max = MAX_CATEGORY_LEN, message = "A categoria deve ter no máximo " + MAX_CATEGORY_LEN + " caracteres")
    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    @Column(nullable = false, precision = PRICE_PRECISION, scale = PRICE_SCALE)
    @DecimalMin(value = "0.01", message = "Preço deve ser pelo menos 1 centavo")
    @DecimalMax(value = "99999999.99", message = "Preço não pode chegar a 100 milhões ")
    @Digits(integer = PRICE_PRECISION - PRICE_SCALE, fraction = PRICE_SCALE, message = "Formato de preço inválido.")
    private BigDecimal price;

    @Column(nullable = false)
    @Min(value = 0, message = "O estoque deve ser maior ou igual a zero")
    private Integer stock;

    public ProductDto toDto() {
        return new ProductDto(id, description, category, price, stock);
    }

    public static Product fromUpsertDto(UpsertProductDto upsertProductDto) {
        return new Product(upsertProductDto.description(), upsertProductDto.category(), upsertProductDto.price(), upsertProductDto.quantity());
    }
}