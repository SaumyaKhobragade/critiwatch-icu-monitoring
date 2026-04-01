package com.example.critiwatch.services;

import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LocalPredictionEngine {

    private LocalPredictionEngine() {
    }

    public static PredictionResult evaluate(VitalSign vitalSign) {
        if (vitalSign == null) {
            return new PredictionResult(
                    Constants.RISK_STABLE,
                    0.0d,
                    "Patient is currently stable based on available vital signs.",
                    Collections.singletonList("No vital sign record available.")
            );
        }

        return evaluate(
                vitalSign.getHeartRate(),
                vitalSign.getSpo2(),
                vitalSign.getSystolicBp(),
                vitalSign.getDiastolicBp(),
                vitalSign.getRespiratoryRate(),
                vitalSign.getTemperature()
        );
    }

    public static PredictionResult evaluate(
            int heartRate,
            int spo2,
            int systolicBp,
            int diastolicBp,
            int respiratoryRate,
            double temperatureInput
    ) {
        List<String> factors = new ArrayList<>();
        int warningHits = 0;
        int criticalHits = 0;
        double score = 0.0d;

        double temperatureCelsius = toCelsius(temperatureInput);

        if (spo2 > 0 && spo2 < 90) {
            score += 35.0d;
            criticalHits++;
            factors.add("Low SpO2");
        } else if (spo2 > 0 && spo2 < 94) {
            score += 18.0d;
            warningHits++;
            factors.add("Borderline SpO2");
        }

        if (systolicBp > 0 && systolicBp < 90) {
            score += 32.0d;
            criticalHits++;
            factors.add("Low systolic blood pressure");
        } else if (systolicBp > 0 && systolicBp < 100) {
            score += 14.0d;
            warningHits++;
            factors.add("Reduced systolic blood pressure");
        }

        if (heartRate > 120) {
            score += 16.0d;
            warningHits++;
            factors.add("High heart rate");
        } else if (heartRate > 0 && heartRate < 50) {
            score += 12.0d;
            warningHits++;
            factors.add("Low heart rate");
        }

        if (respiratoryRate > 30) {
            score += 18.0d;
            warningHits++;
            factors.add("Very high respiratory rate");
        } else if (respiratoryRate > 24) {
            score += 14.0d;
            warningHits++;
            factors.add("High respiratory rate");
        }

        if (temperatureCelsius >= 39.0d) {
            score += 14.0d;
            warningHits++;
            factors.add("High fever");
        } else if (temperatureCelsius >= 38.0d) {
            score += 10.0d;
            warningHits++;
            factors.add("Elevated temperature");
        }

        if (diastolicBp > 0 && diastolicBp < 60) {
            score += 8.0d;
            warningHits++;
            factors.add("Low diastolic blood pressure");
        }

        int abnormalCount = warningHits + criticalHits;
        if (abnormalCount >= 2) {
            score += 8.0d;
        }
        if (criticalHits >= 2) {
            score += 12.0d;
            factors.add("Multiple critical parameters");
        }

        score = Math.max(0.0d, Math.min(100.0d, score));

        String riskLevel;
        if (score >= 70.0d || criticalHits >= 2) {
            riskLevel = Constants.RISK_CRITICAL;
        } else if (score >= 30.0d || criticalHits == 1 || warningHits >= 2) {
            riskLevel = Constants.RISK_WARNING;
        } else {
            riskLevel = Constants.RISK_STABLE;
        }

        String summary = buildSummary(riskLevel, factors);
        return new PredictionResult(riskLevel, score, summary, factors);
    }

    private static double toCelsius(double temperatureInput) {
        if (temperatureInput <= 0.0d) {
            return 0.0d;
        }
        if (temperatureInput > 45.0d) {
            return (temperatureInput - 32.0d) * 5.0d / 9.0d;
        }
        return temperatureInput;
    }

    private static String buildSummary(String riskLevel, List<String> factors) {
        if (factors == null || factors.isEmpty()) {
            return "Patient is currently stable based on available vital signs.";
        }

        String factorText;
        if (factors.size() == 1) {
            factorText = factors.get(0).toLowerCase(Locale.US);
        } else if (factors.size() == 2) {
            factorText = factors.get(0).toLowerCase(Locale.US)
                    + " and " + factors.get(1).toLowerCase(Locale.US);
        } else {
            factorText = factors.get(0).toLowerCase(Locale.US)
                    + ", " + factors.get(1).toLowerCase(Locale.US)
                    + ", and other abnormalities";
        }

        if (Constants.RISK_CRITICAL.equalsIgnoreCase(riskLevel)) {
            return "Patient is at critical risk due to " + factorText + ".";
        }
        if (Constants.RISK_WARNING.equalsIgnoreCase(riskLevel)) {
            return "Patient shows moderate deterioration risk due to " + factorText + ".";
        }
        return "Patient is currently stable based on available vital signs.";
    }

    public static final class PredictionResult {
        private final String riskLevel;
        private final double riskScore;
        private final String summary;
        private final List<String> factors;

        public PredictionResult(String riskLevel, double riskScore, String summary, List<String> factors) {
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.summary = summary;
            this.factors = factors == null ? new ArrayList<>() : new ArrayList<>(factors);
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public double getRiskScore() {
            return riskScore;
        }

        public String getSummary() {
            return summary;
        }

        public List<String> getFactors() {
            return new ArrayList<>(factors);
        }
    }
}
