package com.fitcheck.fit_check.dto.health;

public record HealthCheckResponse(
        String status,
        String profile,
        String name,
        String version,
        long timestamp,                                                
        String message) {}