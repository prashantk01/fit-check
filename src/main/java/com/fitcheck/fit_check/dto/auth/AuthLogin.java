package com.fitcheck.fit_check.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthLogin(
                @JsonProperty("username") @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters") @NotNull(message = "Username is required") String username,
                @JsonProperty("password") @Size(min = 5, max = 20) @NotNull(message = "Password is required") String password) {
}