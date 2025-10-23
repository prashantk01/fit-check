package com.fitcheck.fit_check.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreate(
                @NotBlank(message = "Username is required") @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters") String username,
                @Email(message = "Email must be a valid email address") @NotBlank(message = "Email is required") @Size(max = 50, message = "Email must not exceed 50 characters") String email,
                @NotBlank(message = "Password is required") @Size(min = 5, max = 20, message = "Password must be between 8 and 64 characters") String password) {
}