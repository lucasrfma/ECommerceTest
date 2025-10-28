package com.ecommerce.test.accountservice.services;

import com.ecommerce.test.shared.dtos.AccountInfoDto;
import com.ecommerce.test.shared.dtos.AccountRegistrationDto;
import com.ecommerce.test.shared.dtos.LoginDto;
import com.ecommerce.test.accountservice.entities.Account;
import com.ecommerce.test.accountservice.repositories.AccountRepository;
import com.ecommerce.test.accountservice.results.LoginResult;
import com.ecommerce.test.shared.utils.JwtUtil;
import com.ecommerce.test.shared.exceptions.ManualValidationException;
import com.ecommerce.test.shared.results.ApiResult;
import com.ecommerce.test.shared.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
public class AccountService {

    static final int MIN_PW_SIZE = 8;
    static final int MAX_PW_SIZE = 30;

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public static final String ACCOUNT_ALREADY_EXISTS = "Já existe uma conta com esse e-mail.";
    public static final String BAD_CREDENTIALS = "Erro de email ou senha.";

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
                          @Value("${jwt.secret}") String jwtSecret, @Value("${jwt.duration}") Duration duration) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = new JwtUtil(jwtSecret, duration);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= MIN_PW_SIZE && password.length() <= MAX_PW_SIZE;
    }

    private AccountInfoDto registerAccount_(AccountRegistrationDto accountDto) {
        if (!isValidPassword(accountDto.password())) {
            throw new ManualValidationException("A senha deve ter entre " + MIN_PW_SIZE + " e "
                    + MAX_PW_SIZE + " caracteres.");
        }
        if (accountRepository.existsByEmail(accountDto.email())) {
            throw new ManualValidationException(ACCOUNT_ALREADY_EXISTS);
        }
        String hashedPassword = passwordEncoder.encode(accountDto.password());

        return accountRepository.save(
                new Account(accountDto.email(), hashedPassword, accountDto.address())
        ).toInfoDto();
    }

    public ApiResult<AccountInfoDto> registerAccount(AccountRegistrationDto accountDto) {
        return DbUtils.ProtectDbFunction(this::registerAccount_).apply(accountDto);
    }

    public LoginResult login(LoginDto loginDto) {
        Optional<Account> existingAccount = accountRepository.findByEmail(loginDto.email());
        if (existingAccount.isPresent()) {
            Account account = existingAccount.get();
            if (passwordEncoder.matches(loginDto.password(), account.getPassword())) {
                return new LoginResult.Success(jwtUtil.generateToken(account.getId(), account.getEmail()));
            }
        }
        return new LoginResult.Failure(BAD_CREDENTIALS);
    }
}
