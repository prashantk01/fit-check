package com.fitcheck.fit_check.model.profile;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Document(collection = "weights")
public class WeightEntry {

    @Id
    private String id;
    @NotNull
    private String userId;

    @NotNull
    @Positive(message = "Weight must be positive")
    private double weightKg;

    @NotNull
    private Instant timestamp = Instant.now();

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
