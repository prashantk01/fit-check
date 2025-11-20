package com.fitcheck.fit_check.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BmiServiceTest {

    private final BmiService bmiService = new BmiService();
    
    @Test
    @DisplayName("UT: Calculate BMI correctly for valid weight and height")
    public void shouldCalculateCorrectBMI() {
        double weightKg = 72.0;
        double heightCm = 167.0;
        double heightM = heightCm / 100.0;
        double expectedBMI = weightKg / (heightM * heightM);
        Double bmi = bmiService.calculateBMI(weightKg, heightCm);
        Assertions.assertNotNull(bmi);
        Assertions.assertEquals(expectedBMI, bmi, 0.001);
    }

    @Test
    @DisplayName("UT: Throw exception for non-positive weight")
    public void shouldThrowExceptionForNonPositiveWeight() {
        double weightKg = 0.0;
        double heightCm = 170.0;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bmiService.calculateBMI(weightKg, heightCm);
        });
    }

    @Test
    @DisplayName("UT: Throw exception for non-positive height")
    public void shouldThrowExceptionForNonPositiveHeight() {
        double weightKg = 70.0;
        double heightCm = -5.0;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bmiService.calculateBMI(weightKg, heightCm);
        });
    }

    @Test
    @DisplayName("UT: Throw exception for non-positive weight and height")
    public void shouldThrowExceptionForNonPositiveWeightAndHeight() {
        double weightKg = -10.0;
        double heightCm = 0.0;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bmiService.calculateBMI(weightKg, heightCm);
        });
    }
}
