package com.fitcheck.fit_check.dto.user;

import java.util.Set;

import com.fitcheck.fit_check.enums.Roles;

public record UserResponse(
        String id,
        String username,
        String email,
        Set<Roles> role,
        String authProvider) {
}