package com.fitcheck.fit_check.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.fitcheck.fit_check.dto.profile.ProfileResponse;
import com.fitcheck.fit_check.dto.profile.ProfileUpdate;
import com.fitcheck.fit_check.dto.weight.WeightAdd;
import com.fitcheck.fit_check.dto.weight.WeightResponse;
import com.fitcheck.fit_check.enums.ProgressStatus;
import com.fitcheck.fit_check.exception.ResourceNotFoundException;
import com.fitcheck.fit_check.mapper.WeightMapper;
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
        ProfileResponse profileResponse = profileService.findByUserId(userId);
        return getWeightResponseWithCalculations(weightEntry, profileResponse);
    }

    public Double calculateBMI(double weightKg, double heightCm) {
        Double bmi = null;
        if (weightKg > 0 && heightCm > 0) {
            double heightM = heightCm / 100.0;
            bmi = weightKg / (heightM * heightM);
        }
        return bmi;
    }

    private void updateProfileWeight(String userId, double weightKg) {
        ProfileResponse profileResponse = profileService.findByUserId(userId);
        ProfileUpdate profileUpdate = new ProfileUpdate(
                null, null, null, null, null,
                weightKg, null, null, null, null);
        profileService.updateProfile(profileResponse.id(), profileUpdate);
    }

    public List<WeightResponse> getWeightEntriesOfAUser(String userId) {
        userService.checkAndValidateUserById(userId);
        List<WeightEntry> weightEntries = weightRepository.findByUserIdOrderByTimestampDesc(userId);
        if (weightEntries.isEmpty()) {
            return List.of();
        }
        ProfileResponse profileResponse = profileService.findByUserId(userId);
        double userCurrentHeightCm = profileResponse.heightCm();
        double targetWeightKg = profileResponse.targetWeightKg();

        if (targetWeightKg <= 0 || userCurrentHeightCm <= 0) {
            return weightEntries.stream()
                    .map(entry -> {
                        Double bmi = calculateBMI(entry.getWeightKg(), userCurrentHeightCm);
                        return WeightMapper.toResponse(entry, bmi, null, null, null);
                    })
                    .collect(Collectors.toList());
        }

        List<WeightResponse> weightResponses = weightEntries.stream()
                .map(entry -> getWeightResponseWithCalculations(entry, profileResponse))
                .collect(Collectors.toList());

        return weightResponses;
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

    private WeightResponse getWeightResponseWithCalculations(WeightEntry entry, ProfileResponse profileResponse) {
        Double userCurrentHeightCm = profileResponse.heightCm();
        Double targetWeightKg = profileResponse.targetWeightKg();
        Double bmiValue = null;
        if (userCurrentHeightCm != null && userCurrentHeightCm > 0) {
            bmiValue = calculateBMI(entry.getWeightKg(), userCurrentHeightCm);
        }
        Double differenceWithTargetKg = null;
        ProgressStatus progressStatus = null;
        if (targetWeightKg != null && targetWeightKg > 0) {
            differenceWithTargetKg = entry.getWeightKg() - targetWeightKg;
            if (differenceWithTargetKg > 0) {
                progressStatus = ProgressStatus.ABOVE_TARGET;
            } else if (differenceWithTargetKg < 0) {
                progressStatus = ProgressStatus.BELOW_TARGET;
            } else {
                progressStatus = ProgressStatus.TARGET_ACHIEVED;
            }
        }
        return WeightMapper.toResponse(entry, bmiValue, targetWeightKg, differenceWithTargetKg, progressStatus);
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
        if (weightEntries.isEmpty()) {
            return List.of();
        }
        ProfileResponse profileResponse = profileService.findByUserId(userId);
        Double userCurrentHeightCm = profileResponse.heightCm();
        Double targetWeightKg = profileResponse.targetWeightKg();
        Double bmi = null;
        if ((targetWeightKg == null || targetWeightKg <= 0)
                || (userCurrentHeightCm == null || userCurrentHeightCm <= 0)) {
            return weightEntries.stream()
                    .map(entry -> {
                        return WeightMapper.toResponse(entry, bmi, null, null, null);
                    })
                    .collect(Collectors.toList());
        }

        List<WeightResponse> weightResponses = weightEntries.stream()
                .map(entry -> getWeightResponseWithCalculations(entry, profileResponse))
                .collect(Collectors.toList());

        return weightResponses;
    }

}
