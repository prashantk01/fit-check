package com.fitcheck.fit_check.service;

import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fitcheck.fit_check.model.user.User;

import com.fitcheck.fit_check.dto.auth.AuthLogin;
import com.fitcheck.fit_check.dto.auth.AuthRegister;
import com.fitcheck.fit_check.dto.auth.AuthResponse;
import com.fitcheck.fit_check.exception.BadCredentialException;
import com.fitcheck.fit_check.repository.AuthRepository;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    AuthService(AuthRepository authRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authRepository = authRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    };

    public AuthResponse login(AuthLogin authLogin) {
        Optional<User> user = authRepository.findByUsername(authLogin.username());
        if (user.isEmpty()) {
            throw new BadCredentialException("Invalid username or password");
        }
        if (bCryptPasswordEncoder.matches(authLogin.password(), user.get().getPassword()) == false) {
            throw new BadCredentialException("Invalid username or password");
        }
        // Here you would normally validate the password and generate a token
        String token = "dummy-fitcheck-jwt-token"; // Replace with actual token generation logic
        return new AuthResponse(
                user.get().getUsername(),
                user.get().getEmail(),
                user.get().getRoles(),
                token,
                "Bearer",
                3600L // Token expiry time in seconds
        );
    }

    public AuthResponse register(AuthRegister authRegister) {
        Optional<User> existingUser = authRepository.findByUsername(authRegister.username());
        if (existingUser.isPresent()) {
            throw new BadCredentialException("Username is already taken");
        }
        Optional<User> existingEmail = authRepository.findByEmail(authRegister.email());
        if (existingEmail.isPresent()) {
            throw new BadCredentialException("Email is already registered");
        }
        if (authRegister.username().length() < 5 || authRegister.username().length() > 20) {
            throw new BadCredentialException("Username must be between 5 and 20 characters");
        }
        if (!authRegister.email().matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new BadCredentialException("Email must be a valid email address");
        }
        if (authRegister.password().length() < 8 || authRegister.password().length() > 64) {
            throw new BadCredentialException("Password must be at least 8 characters long");
        }
        if (!authRegister.password().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[+@#$&%]).{8,}$")) {
            throw new BadCredentialException(
                    "Password must contain at least one lowercase character, one uppercase character, one number, and one special character (+@#$&%) and be at least 8 characters long");
        }
        User newUser = new User();
        newUser.setUsername(authRegister.username());
        newUser.setEmail(authRegister.email());
        newUser.setRoles(Set.of("USER")); // Default role
        newUser.setPassword(bCryptPasswordEncoder.encode(authRegister.password()));
        authRepository.save(newUser);
        AuthResponse authResponse = new AuthResponse(
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getRoles(),
                "dummy-fitcheck-jwt-token", // Replace with actual token generation logic
                "Bearer",
                3600L // Token expiry time in seconds
        );
        return authResponse;
    }

}