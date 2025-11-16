package com.fitcheck.fit_check.dto.weight;

import java.time.OffsetDateTime;
import com.fitcheck.fit_check.enums.ProgressStatus;

public record WeightResponse(
                String id,
                OffsetDateTime timestamp,
                double weightKg,
                Double bodyMassIndex,
                Double targetWeightKg,
                Double differenceWithTargetKg,
                ProgressStatus status) {
}
