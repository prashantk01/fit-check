package com.fitcheck.fit_check.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.fitcheck.fit_check.model.profile.Profile;

public interface ProfileRepository extends MongoRepository<Profile, String> {

    // Find profile by user ID
    Optional<Profile> findByUserId(String userId);

    // Check if a profile exists for a given user ID
    boolean existsByUserId(String userId);

    // Delete profile by user ID
    void deleteByUserId(String userId);

}