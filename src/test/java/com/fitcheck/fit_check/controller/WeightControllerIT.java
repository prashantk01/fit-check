package com.fitcheck.fit_check.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import com.fitcheck.fit_check.IntegrationTestBase;
import com.fitcheck.fit_check.repository.ProfileRepository;
import com.fitcheck.fit_check.repository.UserRepository;
import com.fitcheck.fit_check.repository.WeightRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheck.fit_check.dto.auth.AuthRegister;
import com.fitcheck.fit_check.dto.profile.ProfileCreate;
import com.fitcheck.fit_check.dto.weight.WeightAdd;
import com.fitcheck.fit_check.enums.Gender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class WeightControllerIT extends IntegrationTestBase {

    @Autowired
    WebApplicationContext context;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    WeightRepository weightRepository;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    private String testUserId;
    private String testUsername = "testUser";
    private String jwtToken;
    private String testProfileId;

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @BeforeEach
    void setup() throws Exception {
        // Clean test DB
        userRepository.deleteAll();
        profileRepository.deleteAll();
        weightRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test user
        AuthRegister authRegister = new AuthRegister(testUsername, "test@email.com", "testPass@123");
        String userResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(authRegister)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Extract JWT token
        jwtToken = objectMapper.readTree(userResponse).get("token").asText();
        testUserId = userRepository.findByUsername(testUsername)
                .orElseThrow(() -> new RuntimeException("Test setup failed"))
                .getId()
                .toString();

        // Create profile for test user
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        ProfileCreate profileCreate = new ProfileCreate("Prashant Kumar", "fitness freak", "", Gender.MALE,
                dateOfBirth,
                100.0, 170.0, 65.0, false, null);
        String profileResponse = mockMvc.perform(post("/api/profiles")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(profileCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andReturn()
                .getResponse()
                .getContentAsString();
        testProfileId = objectMapper.readTree(profileResponse).get("id").asText();
    }

    private WeightAdd weightAddEntry() {
        OffsetDateTime timestamp = OffsetDateTime.now();
        return new WeightAdd(timestamp, 65.0);
    }

    @Test
    @DisplayName("IT: Test adding a weight entry")
    public void shouldAddWeightEntryForValidUserWithProfile() throws Exception {
        mockMvc.perform(post("/api/weights")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(weightAddEntry())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.weightKg").value(65.0))
                .andExpect(jsonPath("$.bodyMassIndex").exists())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @DisplayName("IT: Should reject invalid weight value")
    void shouldRejectInvalidWeight() throws Exception {
        WeightAdd invalid = new WeightAdd(OffsetDateTime.now(), -10.0);
        mockMvc.perform(post("/api/weights")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(invalid)))
                .andExpect(status().isBadRequest());
        // .andExpect(jsonPath("$.message").value("Weight must be greater than zero"));
    }

    @Test
    @DisplayName("IT: Should not add weight without Auth")
    void shouldNotAddWeightWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/weights")
                .param("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(weightAddEntry())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("IT: Should fetch weight history for user")
    void shouldFetchWeightHistory() throws Exception {
        // Add a weight entry first
        mockMvc.perform(post("/api/weights")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(weightAddEntry())))
                .andExpect(status().isCreated());

        // Fetch history
        mockMvc.perform(get("/api/weights")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].weightKg").value(65.0));
    }

}
