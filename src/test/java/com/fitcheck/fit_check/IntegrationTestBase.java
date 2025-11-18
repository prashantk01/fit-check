package com.fitcheck.fit_check;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "jwt.secret=test-secret-key-minimum-256-bits-long-for-hmac-sha-algorithm-security-jwt-secret",
        "jwt.expirationMs=3600000"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestBase {
    // Embedded MongoDB will start automatically
}