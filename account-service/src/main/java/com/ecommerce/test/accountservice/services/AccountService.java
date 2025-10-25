package com.ecommerce.test.accountservice.services;

import com.ecommerce.test.accountservice.dtos.AccountRegistrationDto;
import com.ecommerce.test.accountservice.dtos.LoginDto;
import com.ecommerce.test.accountservice.entities.Account;
import com.ecommerce.test.accountservice.repositories.AccountRepository;
import com.ecommerce.test.accountservice.results.LoginResult;
import com.ecommerce.test.accountservice.results.RegistrationResult;
import com.ecommerce.test.accountservice.utils.JwtUtil;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class AccountService {

    static final int MIN_PASSWORD_CHAR_LEN = 8;
    static final int MAX_PASSWORD_BYTE_SIZE = 72;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public RegistrationResult registerAccount(AccountRegistrationDto accountDto) {
        Optional<String> validationError = validatePassword(accountDto.password());
        if (validationError.isPresent()) {
            return new RegistrationResult.ValidationError(validationError.get());
        }

        if (accountRepository.existsByEmail(accountDto.email())) {
            return new RegistrationResult.EmailAlreadyExists();
        }

        try {
            String hashedPassword = passwordEncoder.encode(accountDto.password());

            accountRepository.save(new Account(accountDto.email(), hashedPassword, accountDto.address()));
            return new RegistrationResult.Success();
        } catch (ConstraintViolationException e) {
            String violations = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Erro de validação");
            return new RegistrationResult.ValidationError(violations);
        } catch (Exception e) {
            return new RegistrationResult.UnknownError();
        }
    }

    public LoginResult login(LoginDto loginDto) {
        Optional<Account> existingAccount = accountRepository.findByEmail(loginDto.email());
        if (existingAccount.isPresent()) {
            Account account = existingAccount.get();
            if (passwordEncoder.matches(loginDto.password(), account.getPassword())) {
                return new LoginResult.Success(jwtUtil.generateToken(account.getId(), account.getEmail()));
            }
        }
        return new LoginResult.Failure("Erro de email ou senha.");
    }

    private Optional<String> validatePassword(String password) {
        int pwBytes = password.getBytes(StandardCharsets.UTF_8).length;
        if (password.length() < MIN_PASSWORD_CHAR_LEN) {
            return Optional.of("Senha tem que ter pelo menos " + MIN_PASSWORD_CHAR_LEN + " caracteres");
        }
        if (pwBytes > MAX_PASSWORD_BYTE_SIZE) {
            return Optional.of("Senha grande demais. No máximo 72 caracteres. Caracteres especiais podem reduzir esse limite.");
        }
        return Optional.empty();
    }
}
