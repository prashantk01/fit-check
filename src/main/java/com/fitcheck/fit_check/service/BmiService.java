package com.fitcheck.fit_check.service;

public class BmiService {

    public Double calculateBMI(double weightKg, double heightCm) {
        Double bmi = null;
        if (weightKg <= 0 || heightCm <= 0) {
            throw new IllegalArgumentException("Weight and height must be greater than zero");
        }
        if (weightKg > 0 && heightCm > 0) {
            double heightM = heightCm / 100.0;
            bmi = weightKg / (heightM * heightM);
        }
        return bmi;
    }
}
