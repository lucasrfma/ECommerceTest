package com.ecommerce.test.accountservice.controllers;

import com.ecommerce.test.accountservice.dtos.AccountDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @PostMapping("/register")
    public ResponseEntity<String> RegisterAccount(@RequestBody AccountDto userDto) {
        return ResponseEntity.ok("registering");
    }

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("hello world");
    }
}
