package com.fitcheck.fit_check.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheck.fit_check.IntegrationTestBase;
import com.fitcheck.fit_check.dto.auth.AuthRegister;
import com.fitcheck.fit_check.dto.profile.ProfileCreate;
import com.fitcheck.fit_check.enums.Gender;
import com.fitcheck.fit_check.repository.ProfileRepository;
import com.fitcheck.fit_check.repository.UserRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class ProfileControllerIT extends IntegrationTestBase {

        @Autowired
        private WebApplicationContext context;

        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private ProfileRepository profileRepository;

        @Autowired
        private UserRepository userRepository;

        private String testUserId;
        private String testUsername = "testUser";
        private String jwtToken;

        @BeforeEach
        void setUp() throws Exception {
                // Clean up
                profileRepository.deleteAll();
                userRepository.deleteAll();

                // Set up MockMvc
                this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                // Create test user
                AuthRegister authRegister = new AuthRegister(testUsername, "test@email.com", "testPass@123");
                String response = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(authRegister)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                // Extract JWT token
                jwtToken = objectMapper.readTree(response).get("token").asText();
                testUserId = userRepository.findByUsername(testUsername)
                                .orElseThrow(() -> new RuntimeException("Test setup failed"))
                                .getId()
                                .toString();
        }

        private String toJson(Object obj) throws Exception {
                return objectMapper.writeValueAsString(obj);
        }

        private ProfileCreate defaultProfile() {
                LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
                ProfileCreate profileCreate = new ProfileCreate("Prashant Kumar", "fitness freak", "", Gender.MALE,
                                dateOfBirth,
                                100.0, 170.0, 65.0, false, null);
                return profileCreate;
        }

        @Test
        @DisplayName("IT: Should create Profile for existing User")
        public void shouldCreateUserProfileForExistingUser() throws Exception {
                LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
                ProfileCreate profileCreate = new ProfileCreate("Prashant Kumar", "fitness freak", "", Gender.MALE,
                                dateOfBirth,
                                100.0, 170.0, 65.0, false, null);

                mockMvc.perform(post("/api/profiles")
                                .header("Authorization", "Bearer " + jwtToken)
                                .param("userId", testUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(profileCreate)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(testUserId))
                                .andExpect(jsonPath("$.name").value("Prashant Kumar"))
                                .andExpect(jsonPath("$.bio").value("fitness freak"))
                                .andExpect(jsonPath("$.gender").value(Gender.MALE.toString()));
        }

        @Test
        @DisplayName("IT: Should not create Profile for Non-existing User")
        public void shouldNotCreateUserProfileForExistingUser() throws Exception {

                mockMvc.perform(post("/api/profiles")
                                .header("Authorization", "Bearer " + jwtToken)
                                .param("userId", "dummyUserId")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(defaultProfile())))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("IT: Should not create two Profile of a User")
        public void shouldNotCreateTwoProfileOfAUser() throws Exception {
                // First profile creation should succeed
                mockMvc.perform(post("/api/profiles")
                                .header("Authorization", "Bearer " + jwtToken)
                                .param("userId", testUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(defaultProfile())))
                                .andExpect(status().isOk());

                // second profile creation should fail with DuplicateKeyException
                mockMvc.perform(post("/api/profiles")
                                .header("Authorization", "Bearer " + jwtToken)
                                .param("userId", testUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(defaultProfile())))
                                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("IT: Get Profile by ID")
        public void shouldFetchProfileByIdSuccessfully() throws Exception {
                LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
                ProfileCreate profileCreate = new ProfileCreate("Prashant Kumar", "fitness freak", "", Gender.MALE,
                                dateOfBirth,
                                100.0, 170.0, 65.0, false, null);
                // First profile creation should succeed
                String response = mockMvc.perform(post("/api/profiles")
                                .header("Authorization", "Bearer " + jwtToken)
                                .param("userId", testUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(profileCreate)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                String profileId = objectMapper.readTree(response).get("id").asText();
                // Fetch profile by ID
                mockMvc.perform(get("/api/profiles/{id}", profileId)
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(profileId))
                                .andExpect(jsonPath("$.userId").value(testUserId))
                                .andExpect(jsonPath("$.name").value("Prashant Kumar"));
        }

        @Test
        @DisplayName("IT: Should return 401 without auth token")
        void shouldReturn401WithoutAuth() throws Exception {
                mockMvc.perform(post("/api/profiles")
                                .param("userId", testUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(defaultProfile())))
                                .andExpect(status().isUnauthorized());
        }
}
