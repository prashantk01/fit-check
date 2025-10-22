package com.fitcheck.fit_check.dto.profile;

import java.time.LocalDate;
import java.time.LocalTime;
import com.fitcheck.fit_check.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileUpdate(
                @JsonProperty("name") String name,
                @JsonProperty("bio") String bio,
                @JsonProperty("profilePictureUrl") String profilePictureUrl,
                @JsonProperty("gender") Gender gender,
                @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
                @JsonProperty("weightKg") Double weightKg,
                @JsonProperty("heightCm") Double heightCm,
                @JsonProperty("targetWeightKg") Double targetWeightKg,
                @JsonProperty("isReminderEnabled") Boolean isReminderEnabled,
                @JsonProperty("reminderTime") LocalTime reminderTime) {
}