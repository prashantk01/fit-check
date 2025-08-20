package com.fitcheck.fit_check.service;

import java.util.Optional;

import com.fitcheck.fit_check.model.profile.Profile;
import com.fitcheck.fit_check.repository.ProfileRepository;

public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // Method to create a new profile
    public Profile createProfile(Profile profile) {
        Profile createdProfile = profileRepository.save(profile);
        return createdProfile;
    }

    // Method to find a profile by user ID
    public Optional<Profile> findByUserId(String userId) {
        return profileRepository.findByUserId(userId);
    }

    // Method to check if a profile exists for a given user ID
    public boolean existsByUserId(String userId) {
        return profileRepository.existsByUserId(userId);
    }
}