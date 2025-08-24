package com.fitcheck.fit_check.dto.profile;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProfileCreate(
                @NotNull(message = "Name cannot be null") String name,
                @Size(max = 100, message = "Bio must not exceed 100 characters") String bio,
                String profilePictureUrl,
                @NotNull(message = "Gender is required for accurate BMI calculation") String gender,
                @NotNull(message = "Date of birth is required for accurate BMI calculation") LocalDate dateOfBirth,
                @Positive(message = "Weight must be positive and required for accurate BMI calculation") double weightKg,
                @Positive(message = "Height must be positive and required for accurate BMI calculation") double heightCm,
                @Positive(message = "Target Weight must be positive") double targetWeightKg,
                boolean isReminderEnabled,
                @NotNull(message = "Reminder time required when reminder is enabled") LocalTime reminderTime) {
};