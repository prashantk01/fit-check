package com.fitcheck.fit_check.dto.auth;

import java.util.Set;

import com.fitcheck.fit_check.enums.Roles;

public record AuthResponse(
                String username,
                String email,
                Set<Roles> role,
                String token,
                String tokenType,
                Long expiresIn) {
}