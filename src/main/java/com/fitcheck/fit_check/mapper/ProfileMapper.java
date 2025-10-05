package com.fitcheck.fit_check.mapper;

import com.fitcheck.fit_check.dto.profile.ProfileCreate;
import com.fitcheck.fit_check.dto.profile.ProfileResponse;
import com.fitcheck.fit_check.model.profile.Profile;

public class ProfileMapper {

    public static Profile toEntity(ProfileCreate dto, String userId) {
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setName(dto.name());
        profile.setBio(dto.bio());
        profile.setProfilePictureUrl(dto.profilePictureUrl());
        profile.setGender(dto.gender());
        profile.setDateOfBirth(dto.dateOfBirth());
        profile.setWeightKg(dto.weightKg());
        profile.setHeightCm(dto.heightCm());
        profile.setTargetWeightKg(dto.targetWeightKg());
        profile.setReminderEnabled(dto.isReminderEnabled());
        profile.setReminderTime(dto.reminderTime());
        return profile;
    }

    public static ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getName(),
                profile.getBio(),
                profile.getProfilePictureUrl(),
                profile.getGender().name(),
                profile.getDateOfBirth(),
                profile.getWeightKg(),
                profile.getHeightCm(),
                profile.getTargetWeightKg(),
                profile.isReminderEnabled(),
                profile.getReminderTime());
    }
}
