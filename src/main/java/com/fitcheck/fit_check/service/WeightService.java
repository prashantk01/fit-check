package com.fitcheck.fit_check.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fitcheck.fit_check.dto.profile.ProfileUpdate;
import com.fitcheck.fit_check.dto.weight.WeightAdd;
import com.fitcheck.fit_check.dto.weight.WeightResponse;
import com.fitcheck.fit_check.exception.ResourceNotFoundException;
import com.fitcheck.fit_check.mapper.WeightMapper;
import com.fitcheck.fit_check.model.profile.Profile;
import com.fitcheck.fit_check.model.profile.WeightEntry;
import com.fitcheck.fit_check.repository.WeightRepository;
import com.fitcheck.fit_check.exception.AccessDeniedException;

@Service
public class WeightService {
    private final WeightRepository weightRepository;
    private final UserService userService;
    private final ProfileService profileService;

    public WeightService(WeightRepository weightRepository, UserService userService, ProfileService profileService) {
        this.weightRepository = weightRepository;
        this.userService = userService;
        this.profileService = profileService;
    }

    public WeightResponse addWeightEntry(String userId, WeightAdd weightAdd) {
        userService.checkAndValidateUserById(userId);
        if (weightAdd.weightKg() <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero");
        }
        WeightEntry weightEntry = WeightMapper.toEntity(weightAdd, userId);
        weightRepository.save(weightEntry);
        updateProfileWeight(userId, weightAdd.weightKg());
        return WeightMapper.toResponse(weightEntry);
    }

    private void updateProfileWeight(String userId, double weightKg) {
        Profile profile = profileService.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));

        ProfileUpdate profileUpdate = new ProfileUpdate(
                null, null, null, null, null,
                weightKg, null, null, null, null);
        profileService.updateProfile(profile.getId(), profileUpdate);
    }

    public List<WeightResponse> getWeightEntriesOfAUser(String userId) {
        userService.checkAndValidateUserById(userId);
        // List<WeightEntry> weightEntries = weightRepository.findByUserId(userId);
        List<WeightEntry> weightEntries = weightRepository.findByUserIdOrderByTimestampDesc(userId);
        return weightEntries.stream()
                .map(WeightMapper::toResponse)
                .collect(Collectors.toList());
    }

    private String getUserIdFromWeightEntryId(String weightEntryId) {
        WeightEntry weightEntry = weightRepository.findById(weightEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Weight entry not found with id: " + weightEntryId));
        return weightEntry.getUserId();
    }

    public void deleteWeightEntry(String userId, String weightEntryId) {
        userService.checkAndValidateUserById(userId);
        if (!getUserIdFromWeightEntryId(weightEntryId).equals(userId)) {
            throw new AccessDeniedException("Weight entry does not belong to user with id: " + userId);
        }
        weightRepository.deleteById(weightEntryId);
    }

    public void deleteAllWeightEntriesOfAUser(String userId) {
        userService.checkAndValidateUserById(userId);
        weightRepository.deleteByUserId(userId);
    }

    public List<WeightResponse> getWeightEntriesOfAUserInDateRange(String userId, OffsetDateTime startDate,
            OffsetDateTime endDate) {
        userService.checkAndValidateUserById(userId);
        startDate = startDate.withOffsetSameInstant(ZoneOffset.UTC);
        endDate = endDate.withOffsetSameInstant(ZoneOffset.UTC);
        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();
        List<WeightEntry> weightEntries = weightRepository.findByUserIdAndTimestampBetween(
                userId,
                startInstant,
                endInstant);
        return weightEntries.stream()
                .map(WeightMapper::toResponse)
                .collect(Collectors.toList());
    }

}
