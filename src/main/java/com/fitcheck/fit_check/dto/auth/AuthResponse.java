package com.fitcheck.fit_check.dto.auth;

import java.util.Set;

public record AuthResponse(
        String username,
        String email,
        Set<String> role,
        String token,
        String tokenType,
        Long expiresIn) {
}