package com.fitcheck.fit_check.dto.profile;

import java.time.LocalDate;
import java.time.LocalTime;

public record ProfileResponse(
        String id,
        String userId,
        String name,
        String bio,
        String profilePictureUrl,
        String gender,
        LocalDate dateOfBirth,
        double weightKg,
        double heightCm,
        double targetWeightKg,
        boolean isReminderEnabled,
        LocalTime reminderTime) {
}
