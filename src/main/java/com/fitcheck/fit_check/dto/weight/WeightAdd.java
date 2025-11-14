package com.fitcheck.fit_check.dto.weight;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WeightAdd(
                OffsetDateTime timestamp,
                @NotNull @Positive(message = "Weight must be positive and in KG") double weightKg) {
}
