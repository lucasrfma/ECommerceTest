package com.ecommerce.test.accountservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Account {
    public Account(String email, String password, String address) {
        this.email = email;
        this.password = password;
        this.address = address;
    }
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
    @NotBlank(message = "E-mail é obrigatório")
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "Endereço é obrigatório")
    private String address;
}
