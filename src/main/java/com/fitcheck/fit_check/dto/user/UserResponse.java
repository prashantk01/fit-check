package com.fitcheck.fit_check.dto.user;

import java.util.Set;

public record UserResponse(
                String id,
                String username,
                String email,
                Set<String> role,
                String authProvider) {
}