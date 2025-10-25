package com.ecommerce.test.accountservice.controllers;

import com.ecommerce.test.accountservice.dtos.AccountRegistrationDto;
import com.ecommerce.test.accountservice.dtos.LoginDto;
import com.ecommerce.test.accountservice.results.LoginResult;
import com.ecommerce.test.accountservice.results.RegistrationResult;
import com.ecommerce.test.accountservice.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    final private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> RegisterAccount(@RequestBody AccountRegistrationDto accountRegistrationDto) {
        RegistrationResult result = accountService.registerAccount(accountRegistrationDto);
        return switch (result) {
            case RegistrationResult.Success ignored ->
                    ResponseEntity.ok("Conta registrada com sucesso");
            case RegistrationResult.EmailAlreadyExists ignored ->
                    ResponseEntity.badRequest().body("Já existe uma conta com esse e-mail");
            case RegistrationResult.ValidationError validationError ->
                    ResponseEntity.badRequest().body(validationError.message());
            case RegistrationResult.UnknownError ignored ->
                    ResponseEntity.internalServerError().body("Erro interno inesperado");
        };
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResult> LoginAccount(@RequestBody LoginDto loginDto) {
        LoginResult result = accountService.login(loginDto);
        return switch (result) {
            case LoginResult.Success success -> ResponseEntity.ok(success);
            case LoginResult.Failure failure -> ResponseEntity.badRequest().body(failure);
        };
    }

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("hello world");
    }
}
