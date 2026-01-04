package com.fitcheck.fit_check.service;

import org.springframework.stereotype.Service;
import com.fitcheck.fit_check.dto.profile.ProfileCreate;
import com.fitcheck.fit_check.dto.profile.ProfileResponse;
import com.fitcheck.fit_check.dto.profile.ProfileUpdate;
import com.fitcheck.fit_check.exception.DuplicateKeyException;
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
    private final UserService userService;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.userService = userService;

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
    public ProfileResponse findByUserId(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with userId: " + userId));
        return ProfileMapper.toResponse(profile);
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

    public double getCurrentWeight(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        double userCurrentWeightKg = profile.getWeightKg();
        if (userCurrentWeightKg <= 0) {
            throw new IllegalStateException("Current weight is not set for user with id: " + userId);
        }
        return userCurrentWeightKg;
    }

    public double getTargetWeight(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        double userTargetWeightKg = profile.getTargetWeightKg();
        if (userTargetWeightKg <= 0) {
            throw new IllegalStateException("Target weight is not set for user with id: " + userId);
        }
        return userTargetWeightKg;
    }

    public double getHeightCm(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        double userHeightCm = profile.getHeightCm();
        if (userHeightCm <= 0) {
            throw new IllegalStateException("Height is not set for user with id: " + userId);
        }
        return userHeightCm;
    }

    public ProfileResponse updateProfile(String id, ProfileUpdate profileUpdateDTO) {
        Profile existingProfile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
        // security check to make sure user updating their own profile
        userService.checkAndValidateUserById(existingProfile.getUserId());
        if (profileUpdateDTO.name() != null)
            existingProfile.setName(profileUpdateDTO.name());
        if (profileUpdateDTO.bio() != null)
            existingProfile.setBio(profileUpdateDTO.bio());
        if (profileUpdateDTO.profilePictureUrl() != null)
            existingProfile.setProfilePictureUrl(profileUpdateDTO.profilePictureUrl());
        if (profileUpdateDTO.gender() != null)
            existingProfile.setGender(profileUpdateDTO.gender());
        if (profileUpdateDTO.dateOfBirth() != null)
            existingProfile.setDateOfBirth(profileUpdateDTO.dateOfBirth());
        if (profileUpdateDTO.weightKg() != null && profileUpdateDTO.weightKg() > 0.0)
            existingProfile.setWeightKg(profileUpdateDTO.weightKg());
        if (profileUpdateDTO.heightCm() != null && profileUpdateDTO.heightCm() > 0.0)
            existingProfile.setHeightCm(profileUpdateDTO.heightCm());
        if (profileUpdateDTO.targetWeightKg() != null && profileUpdateDTO.targetWeightKg() > 0.0)
            existingProfile.setTargetWeightKg(profileUpdateDTO.targetWeightKg());
        if (profileUpdateDTO.isReminderEnabled() != null)
            existingProfile.setReminderEnabled(profileUpdateDTO.isReminderEnabled());
        if (profileUpdateDTO.reminderTime() != null)
            existingProfile.setReminderTime(profileUpdateDTO.reminderTime());
        Profile updatedProfile = profileRepository.save(existingProfile);
        return ProfileMapper.toResponse(updatedProfile);
    }
}