package com.example.critiwatch.utils;

public class RiskUtils {

    private RiskUtils() {
    }

    public static String deriveRiskLevel(
            int heartRate,
            int spo2,
            int systolicBp,
            int respiratoryRate,
            double temperature
    ) {
        boolean critical = heartRate >= 130
                || spo2 <= 88
                || systolicBp <= 85
                || respiratoryRate >= 30
                || temperature >= 39.0;

        if (critical) {
            return Constants.RISK_CRITICAL;
        }

        boolean warning = heartRate >= 110
                || spo2 <= 93
                || systolicBp <= 95
                || respiratoryRate >= 22
                || temperature >= 37.8;

        return warning ? Constants.RISK_WARNING : Constants.RISK_STABLE;
    }

    public static double deriveRiskScore(
            int heartRate,
            int spo2,
            int systolicBp,
            int respiratoryRate,
            double temperature
    ) {
        double score = 30.0;

        if (heartRate >= 130) {
            score += 20.0;
        } else if (heartRate >= 110) {
            score += 10.0;
        }

        if (spo2 <= 88) {
            score += 20.0;
        } else if (spo2 <= 93) {
            score += 10.0;
        }

        if (systolicBp <= 85) {
            score += 15.0;
        } else if (systolicBp <= 95) {
            score += 8.0;
        }

        if (respiratoryRate >= 30) {
            score += 10.0;
        } else if (respiratoryRate >= 22) {
            score += 5.0;
        }

        if (temperature >= 39.0) {
            score += 10.0;
        } else if (temperature >= 37.8) {
            score += 5.0;
        }

        return Math.min(99.0, Math.max(5.0, score));
    }
}
