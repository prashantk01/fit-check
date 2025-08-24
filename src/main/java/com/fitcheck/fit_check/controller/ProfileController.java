package com.fitcheck.fit_check.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheck.fit_check.dto.profile.ProfileCreate;
import com.fitcheck.fit_check.dto.profile.ProfileResponse;
import com.fitcheck.fit_check.service.ProfileService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;

    ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody ProfileCreate profileCreate,
            @RequestParam String userId) {
        ProfileResponse profileResponse = profileService.createProfile(profileCreate, userId);
        return ResponseEntity.ok(profileResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable String id) {
        ProfileResponse profileResponse = profileService.getProfileById(id);
        return ResponseEntity.ok(profileResponse);
    }
}