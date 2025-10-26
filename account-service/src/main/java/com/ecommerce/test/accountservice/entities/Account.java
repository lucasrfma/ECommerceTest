package com.ecommerce.test.accountservice.entities;

import com.ecommerce.test.shared.dtos.AccountInfoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
public class Account {

    static final int MAX_EMAIL_LEN = 100;
    static final int BCRYPTED_PW_SIZE = 60;

    public Account(String email, String password, String address) {
        this.email = email;
        this.password = password;
        this.address = address;
    }
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = MAX_EMAIL_LEN)
    @Email(message = "E-mail inválido")
    @Size(max = MAX_EMAIL_LEN, message = "O e-mail deve ter no máximo " + MAX_EMAIL_LEN + " caracteres")
    @NotBlank(message = "E-mail é obrigatório")
    private String email;

    @Column(nullable = false, length = BCRYPTED_PW_SIZE)
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "Endereço é obrigatório")
    private String address;

    public AccountInfoDto toInfoDto() {
        return new AccountInfoDto(email, address);
    }
}
