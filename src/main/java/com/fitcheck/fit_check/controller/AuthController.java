package com.fitcheck.fit_check.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheck.fit_check.dto.auth.AuthLogin;
import com.fitcheck.fit_check.dto.auth.AuthRegister;
import com.fitcheck.fit_check.dto.auth.AuthResponse;
import com.fitcheck.fit_check.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthLogin authLogin) {
        AuthResponse authResponse = authService.login(authLogin);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody AuthRegister authRegister) {
        AuthResponse authResponse = authService.register(authRegister);
        return ResponseEntity.ok(authResponse);
    }

}