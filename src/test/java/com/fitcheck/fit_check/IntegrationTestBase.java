package com.fitcheck.fit_check;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        // Clear all MongoDB connection properties
        // "spring.data.mongodb.uri=",
        // "spring.data.mongodb.host=",
        // "spring.data.mongodb.port=",
        // "spring.data.mongodb.database=",
        // "spring.data.mongodb.username=",
        // "spring.data.mongodb.password=",

        // // Use embedded MongoDB
        // "spring.mongodb.embedded.version=7.0.14",

        // Disable lazy initialization for tests (faster startup detection of issues)
        // "spring.main.lazy-initialization=false",
        // "spring.data.mongodb.auto-index-creation=true",
        // "spring.mongodb.embedded.version=7.0.14",
        // "spring.autoconfigure.exclude=" +
        // "org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer,"
        // +
        // "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
        // "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",

        // JWT Configuration
        "jwt.secret=test-secret-key-minimum-256-bits-long-for-hmac-sha-algorithm-security-jwt-secret",
        "jwt.expirationMs=3600000"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestBase {
    // Embedded MongoDB will start automatically
}