package com.fitcheck.fit_check.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

        @Value("${app.version}")
        private String appVersion;

        @Bean
        public OpenAPI fitCheckOpenAPI() {
                return new OpenAPI()
                                .info(new io.swagger.v3.oas.models.info.Info()
                                                .title("FitCheck API Documentation")
                                                .version(appVersion)
                                                .description("API documentation for FitCheck backend application")
                                                .contact(new Contact()
                                                                .name("Prashant Kumar")
                                                                .email("kumarprince2510@gmail.com")
                                                                .url("https://github.com/prashantk01"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("http://springdoc.org")))
                                .externalDocs(new ExternalDocumentation()
                                                .description("Project Repository")
                                                .url("https://github.com/prashantk01/fitcheck"))
                                // add JWT
                                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                                .components(new Components()
                                                .addSecuritySchemes("BearerAuth",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")));
        }
}