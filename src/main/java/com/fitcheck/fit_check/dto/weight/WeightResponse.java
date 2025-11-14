package com.fitcheck.fit_check.dto.weight;

import java.time.OffsetDateTime;

public record WeightResponse(
                String id,
                OffsetDateTime timestamp,
                double weightKg) {
}
