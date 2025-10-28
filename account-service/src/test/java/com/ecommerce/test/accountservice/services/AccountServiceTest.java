package com.ecommerce.test.accountservice.services;

import com.ecommerce.test.accountservice.entities.Account;
import com.ecommerce.test.accountservice.repositories.AccountRepository;
import com.ecommerce.test.accountservice.results.LoginResult;
import com.ecommerce.test.shared.dtos.AccountInfoDto;
import com.ecommerce.test.shared.dtos.AccountRegistrationDto;
import com.ecommerce.test.shared.dtos.LoginDto;
import com.ecommerce.test.shared.results.ApiResult;
import com.ecommerce.test.shared.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil = new JwtUtil(mockSecret, mockDuration);

    private AccountService accountService;

    private static final String mockSecret = "testFASDsecret123123123123123123123123123123123213123123";
    private static final Duration mockDuration = Duration.ofHours(1);

    @BeforeEach
    void setUp() {
        // Manually inject mocks into AccountService constructor
        accountService = new AccountService(
                accountRepository,
                passwordEncoder,
                mockSecret, // jwtSecret
                mockDuration // jwtDuration
        );
    }
    // Tests for registerAccount

    @Test
    void testRegisterAccount_Success() {
        AccountRegistrationDto registrationDto = new AccountRegistrationDto(
                "test@example.com", "password123", "123 Main St");
        Account account = new Account(
                "test@example.com", "encodedPassword123", "123 Main St");
        account.setId(1L);

        when(passwordEncoder.encode(registrationDto.password())).thenReturn("encodedPassword123");
        when(accountRepository.existsByEmail(registrationDto.email())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        ApiResult<AccountInfoDto> result = accountService.registerAccount(registrationDto);

        assertInstanceOf(ApiResult.Success.class, result);
        ApiResult.Success<AccountInfoDto> successResult = (ApiResult.Success<AccountInfoDto>) result;
        assertEquals(account.toInfoDto(), successResult.getSuccessData());
    }

    @Test
    void testRegisterAccount_InvalidPasswordTooShort() {
        AccountRegistrationDto registrationDto = new AccountRegistrationDto(
                "test@example.com", "short", "123 Main St");

        ApiResult<AccountInfoDto> result = accountService.registerAccount(registrationDto);

        assertInstanceOf(ApiResult.ValidationFailure.class, result);
        ApiResult.ValidationFailure<AccountInfoDto> validationFailure = (ApiResult.ValidationFailure<AccountInfoDto>) result;
        assertTrue(validationFailure.getErrorMessage().contains("senha deve ter entre"));
    }

    @Test
    void testRegisterAccount_InvalidPasswordTooLong() {
        AccountRegistrationDto registrationDto = new AccountRegistrationDto(
                "test@example.com", "verylongpasswordthatiswaytoolongforthisapplication", "123 Main St");

        ApiResult<AccountInfoDto> result = accountService.registerAccount(registrationDto);

        assertInstanceOf(ApiResult.ValidationFailure.class, result);
        ApiResult.ValidationFailure<AccountInfoDto> validationFailure = (ApiResult.ValidationFailure<AccountInfoDto>) result;
        assertTrue(validationFailure.getErrorMessage().contains("senha deve ter entre"));
    }

    @Test
    void testRegisterAccount_EmailAlreadyExists() {
        AccountRegistrationDto registrationDto = new AccountRegistrationDto(
                "existing@example.com", "password123", "123 Main St");

        when(accountRepository.existsByEmail(registrationDto.email())).thenReturn(true);

        ApiResult<AccountInfoDto> result = accountService.registerAccount(registrationDto);

        assertInstanceOf(ApiResult.ValidationFailure.class, result);
        ApiResult.ValidationFailure<AccountInfoDto> validationFailure = (ApiResult.ValidationFailure<AccountInfoDto>) result;
        assertEquals(AccountService.ACCOUNT_ALREADY_EXISTS, validationFailure.getErrorMessage());
    }

    // Tests for login

    @Test
    void testLogin_Success() {
        LoginDto loginDto = new LoginDto("test@example.com", "password123");
        Account account = new Account(
                "test@example.com", "encodedPassword123", "123 Main St");
        account.setId(1L);

        when(accountRepository.findByEmail(loginDto.email())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(loginDto.password(), account.getPassword())).thenReturn(true);

        LoginResult result = accountService.login(loginDto);
        assertInstanceOf(LoginResult.Success.class, result);
        LoginResult.Success successResult = (LoginResult.Success) result;
        String token = successResult.access_token();
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testLogin_IncorrectPassword() {
        LoginDto loginDto = new LoginDto("test@example.com", "wrongpassword");
        Account account = new Account(
                "test@example.com", "encodedPassword123", "123 Main St");
        account.setId(1L);

        when(accountRepository.findByEmail(loginDto.email())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(loginDto.password(), account.getPassword())).thenReturn(false);

        LoginResult result = accountService.login(loginDto);

        assertInstanceOf(LoginResult.Failure.class, result);
        LoginResult.Failure failureResult = (LoginResult.Failure) result;
        assertEquals(AccountService.BAD_CREDENTIALS, failureResult.error_message());
    }

    @Test
    void testLogin_EmailNotFound() {
        LoginDto loginDto = new LoginDto("nonexistent@example.com", "password123");

        when(accountRepository.findByEmail(loginDto.email())).thenReturn(Optional.empty());

        LoginResult result = accountService.login(loginDto);

        assertInstanceOf(LoginResult.Failure.class, result);
        LoginResult.Failure failureResult = (LoginResult.Failure) result;
        assertEquals(AccountService.BAD_CREDENTIALS, failureResult.error_message());
    }
}
