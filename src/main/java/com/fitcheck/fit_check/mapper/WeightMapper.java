package com.fitcheck.fit_check.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fitcheck.fit_check.dto.weight.WeightAdd;
import com.fitcheck.fit_check.dto.weight.WeightResponse;
import com.fitcheck.fit_check.model.profile.WeightEntry;

public class WeightMapper {
    public static WeightEntry toEntity(WeightAdd weightAdd, String userId) {
        WeightEntry weightEntry = new WeightEntry();
        weightEntry.setUserId(userId);
        weightEntry.setWeightKg(weightAdd.weightKg());

        OffsetDateTime timestamp = weightAdd.timestamp();
        if (timestamp == null) {
            timestamp = OffsetDateTime.now(ZoneOffset.UTC);
        } else {
            // Normalize client timestamp to UTC
            timestamp = timestamp.withOffsetSameInstant(ZoneOffset.UTC);
        }
        Instant instantTimestamp = timestamp.toInstant();
        weightEntry.setTimestamp(instantTimestamp);
        return weightEntry;
    }

    public static WeightResponse toResponse(WeightEntry weightEntry) {
        OffsetDateTime timestamp = OffsetDateTime.ofInstant(weightEntry.getTimestamp(), ZoneOffset.UTC);
        return new WeightResponse(
                weightEntry.getId(),
                timestamp,
                weightEntry.getWeightKg());
    }

}
