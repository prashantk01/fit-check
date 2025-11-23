package com.fitcheck.fit_check.dto.profile;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fitcheck.fit_check.enums.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProfileCreate(
                @JsonProperty("name") @NotNull(message = "Name cannot be null") String name,
                @JsonProperty("bio") @Size(max = 100, message = "Bio must not exceed 100 characters") String bio,
                @JsonProperty("profilePictureUrl") String profilePictureUrl,
                @JsonProperty("gender") @NotNull(message = "Gender is required for accurate BMI calculation") @Schema(description = "Gender of the user", example = "MALE") Gender gender,
                @JsonProperty("dateOfBirth") @NotNull(message = "Date of birth is required for accurate BMI calculation") LocalDate dateOfBirth,
                @JsonProperty("weightKg") @NotNull @Positive(message = "Weight must be positive and required for accurate BMI calculation") Double weightKg,
                @JsonProperty("heightCm") @Positive(message = "Height must be positive and required for accurate BMI calculation") Double heightCm,
                @JsonProperty("targetWeightKg") @Positive(message = "Target Weight must be positive") Double targetWeightKg,
                @JsonProperty("isReminderEnabled") @NotNull(message = "isReminderEnabled is required") Boolean isReminderEnabled,
                @JsonProperty("reminderTime") @JsonFormat(pattern = "HH:mm:ss") LocalTime reminderTime) {
}