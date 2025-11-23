package com.fitcheck.fit_check.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheck.fit_check.IntegrationTestBase;
import com.fitcheck.fit_check.dto.auth.AuthLogin;
import com.fitcheck.fit_check.dto.auth.AuthRegister;
import com.fitcheck.fit_check.repository.UserRepository;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class AuthControllerIT extends IntegrationTestBase {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // Clean up before each test
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void deleteAllUsers() {
        userRepository.deleteAll();
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    // helper methods
    private String registerUser(String username, String email, String password) throws Exception {
        AuthRegister authRegister = new AuthRegister(username, email, password);
        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(authRegister)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.role").exists())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // extract token from response JSON
        return objectMapper.readTree(response).get("token").asText();
    }

    private String loginUser(String username, String password) throws Exception {
        AuthLogin authLogin = new AuthLogin(username, password);
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(authLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.role").exists())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        // extract token from response JSON
        return objectMapper.readTree(response).get("token").asText();
    }

    // tests
    @Test
    @DisplayName("IT: Should register user successfully")
    public void shouldRegisterUserSuccessfully() throws Exception {
        AuthRegister testUser = new AuthRegister("testUser", "test@email.com", "TestPassword123@");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.username()))
                .andExpect(jsonPath("$.email").value(testUser.email()))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.role").exists());

    }

    @Test
    @DisplayName("IT: Should login successfully after registration")
    public void shouldLoginSuccessfullyAfterRegistration() throws Exception {
        // register user
        String tokenForRegUser = registerUser("userA", "userA@email.com", "Password@123");

        // login user
        AuthLogin login = new AuthLogin("userA", "Password@123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                // .andExpect(jsonPath("$.token").value(tokenForRegUser))
                .andExpect(jsonPath("$.username").value("userA"));

    }

    @Test
    @DisplayName("IT: should not register for already existing username")
    public void shouldNotRegisterWithExistingUsername() throws Exception {
        // register user
        registerUser("userA", "userA@email.com", "userA@123");
        AuthRegister duplicateAuthRegister = new AuthRegister("userA", "userB@email.com", "userB@123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(duplicateAuthRegister)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    @DisplayName("IT: should not register for already existing email")
    public void shouldNotRegisterWithExistingEmail() throws Exception {
        registerUser("useraB", "userB@email.com", "userB@123");
        AuthRegister duplicateAuthRegister = new AuthRegister("userA", "userB@email.com", "userA@123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(duplicateAuthRegister)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

}
