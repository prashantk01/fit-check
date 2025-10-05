package com.fitcheck.fit_check.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fitcheck.fit_check.dto.profile.ProfileCreate;
import com.fitcheck.fit_check.dto.profile.ProfileResponse;
import org.springframework.dao.DuplicateKeyException;
import com.fitcheck.fit_check.exception.ResourceNotFoundException;
import com.fitcheck.fit_check.mapper.ProfileMapper;
import com.fitcheck.fit_check.model.profile.Profile;
import com.fitcheck.fit_check.model.user.User;
import com.fitcheck.fit_check.repository.ProfileRepository;
import com.fitcheck.fit_check.repository.UserRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;

    }

    public ProfileResponse createProfile(ProfileCreate profileDTO, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (profileRepository.existsByUserId(userId)) {
            throw new DuplicateKeyException("Profile already exists for user with id: " + userId);
        }
        Profile profile = ProfileMapper.toEntity(profileDTO, user.getId());
        Profile createdProfile = profileRepository.save(profile);
        return ProfileMapper.toResponse(createdProfile);
    }

    // Method to find a profile by user ID
    public Optional<Profile> findByUserId(String userId) {
        return profileRepository.findByUserId(userId);
    }

    // Method to check if a profile exists for a given user ID
    public boolean existsByUserId(String userId) {
        return profileRepository.existsByUserId(userId);
    }

    public ProfileResponse getProfileById(String id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
        return ProfileMapper.toResponse(profile);
    }
}