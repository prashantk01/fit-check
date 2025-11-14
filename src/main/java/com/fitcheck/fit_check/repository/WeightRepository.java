package com.fitcheck.fit_check.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fitcheck.fit_check.model.profile.WeightEntry;

public interface WeightRepository extends MongoRepository<WeightEntry, String> {
    boolean existsByUserId(String userId);

    List<WeightEntry> findByUserId(String userId);

    List<WeightEntry> findByUserIdAndTimestampBetween(String userId, Instant start, Instant end);

    void deleteByUserId(String userId);

    List<WeightEntry> findByUserIdOrderByTimestampDesc(String userId);

}
