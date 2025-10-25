package com.ecommerce.test.accountservice.controllers;

import com.ecommerce.test.accountservice.dtos.AccountRegistrationDto;
import com.ecommerce.test.accountservice.dtos.LoginDto;
import com.ecommerce.test.accountservice.results.LoginResult;
import com.ecommerce.test.accountservice.results.RegistrationResult;
import com.ecommerce.test.accountservice.services.AccountService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta registrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "Conta já existe, ou parâmetros inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content)
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem sucedido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResult.Success.class))
            ),
            @ApiResponse(responseCode = "400", description = "Erro de email ou senha.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResult.Failure.class))
            )
    })
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
