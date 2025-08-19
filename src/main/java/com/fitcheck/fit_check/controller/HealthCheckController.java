package com.fitcheck.fit_check.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheck.fit_check.dto.health.HealthCheckResponse;

/**
 * HealthCheckController is a REST controller that provides an endpoint to check the health of the FitCheck application.
 * It returns a simple JSON response indicating the application status, version, and timestamp.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    @Value("${app.version}")
    private String appVersionString;
    
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${spring.application.name:FitCheck}")
    private String appName;

    @GetMapping
    public HealthCheckResponse healthCheck() {
        HealthCheckResponse response = new HealthCheckResponse(
                "UP",
                activeProfile,
                appName,
                appVersionString,
                System.currentTimeMillis(),
                "FitCheck Application is running..!"
        );
        // Log the health check response (optional)
        // logger.info("Health Check Response: {}", response); 
        return response;
    }
}