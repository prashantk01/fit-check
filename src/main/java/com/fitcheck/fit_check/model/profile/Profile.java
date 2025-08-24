package com.fitcheck.fit_check.model.profile;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fitcheck.fit_check.enums.Gender;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Document(collection = "profiles") // Collection name in MongoDB
public class Profile {
    @Id
    private String id;

    @NotNull(message = "Name cannot be null")
    private String name;

    @Indexed(unique = true)
    private String userId;

    @Size(max = 100, message = "Bio must not exceed 100 characters")
    private String bio;

    private String profilePictureUrl;

    @NotNull(message = "Gender is required for accurate BMI calculation")
    private Gender gender;

    @NotNull(message = "Date of birth is required for accurate BMI calculation")
    private LocalDate dateOfBirth; // Format: YYYY-MM-DD

    @Positive(message = "Weight must be positive and required for accurate BMI calculation")
    private double weightKg; // in kg

    @Positive(message = "Height must be positive and required for accurate BMI calculation")
    private double heightCm; // in cm

    @Positive(message = "Target Weight must be positive")
    private double targetWeightKg; // in kg

    @NotNull(message = "Reminder time required when reminder is enabled")
    private LocalTime reminderTime = LocalTime.parse("10:00:00"); // Time for daily reminders, e.g., 08:00 for 8 AM

    private boolean isReminderEnabled = true; // Flag to enable/disable reminders

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public double getTargetWeightKg() {
        return targetWeightKg;
    }

    public void setTargetWeightKg(double targetWeightKg) {
        this.targetWeightKg = targetWeightKg;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isReminderEnabled() {
        return isReminderEnabled;
    }

    public void setReminderEnabled(boolean isReminderEnabled) {
        this.isReminderEnabled = isReminderEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", weightKg=" + weightKg +
                ", heightCm=" + heightCm +
                ", targetWeightKg=" + targetWeightKg +
                ", reminderTime=" + reminderTime +
                ", isReminderEnabled=" + isReminderEnabled +
                '}';
    }
}
