package com.example.critiwatch.utils;

import com.example.critiwatch.models.VitalSign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RiskUtils {

    private RiskUtils() {
    }

    public static EvaluationResult evaluate(
            int heartRate,
            int spo2,
            int systolicBp,
            int diastolicBp,
            int respiratoryRate,
            double temperatureInput
    ) {
        double temperatureCelsius = normalizeToCelsius(temperatureInput);
        List<String> reasons = new ArrayList<>();
        int warningCount = 0;
        int criticalCount = 0;
        int score = 0;

        if (heartRate > 130) {
            criticalCount++;
            score += 22;
            reasons.add("Severe tachycardia (HR > 130 bpm)");
        } else if (heartRate > 120) {
            warningCount++;
            score += 12;
            reasons.add("Elevated heart rate (HR > 120 bpm)");
        } else if (heartRate < 50 && heartRate > 0) {
            warningCount++;
            score += 10;
            reasons.add("Possible bradycardia (HR < 50 bpm)");
        }

        if (spo2 > 0 && spo2 < 90) {
            criticalCount++;
            score += 28;
            reasons.add("Low SpO2 (< 90%)");
        } else if (spo2 > 0 && spo2 < 94) {
            warningCount++;
            score += 16;
            reasons.add("Borderline oxygen saturation (SpO2 < 94%)");
        }

        if (systolicBp > 0 && systolicBp < 90) {
            criticalCount++;
            score += 26;
            reasons.add("Low systolic blood pressure (< 90 mmHg)");
        } else if (systolicBp > 0 && systolicBp < 100) {
            warningCount++;
            score += 12;
            reasons.add("Reduced systolic blood pressure (< 100 mmHg)");
        }

        if (diastolicBp > 0 && diastolicBp < 60) {
            warningCount++;
            score += 8;
            reasons.add("Low diastolic blood pressure (< 60 mmHg)");
        }

        if (respiratoryRate > 30) {
            criticalCount++;
            score += 18;
            reasons.add("Marked tachypnea (RR > 30/min)");
        } else if (respiratoryRate > 24) {
            warningCount++;
            score += 10;
            reasons.add("Elevated respiratory rate (RR > 24/min)");
        }

        if (temperatureCelsius >= 39.0d) {
            criticalCount++;
            score += 14;
            reasons.add("High fever (Temp >= 39.0 C)");
        } else if (temperatureCelsius >= 38.0d) {
            warningCount++;
            score += 9;
            reasons.add("Fever detected (Temp >= 38.0 C)");
        }

        int abnormalCount = warningCount + criticalCount;
        if (abnormalCount >= 2) {
            score += 10;
        }
        if (criticalCount >= 2) {
            score += 14;
            reasons.add("Multiple critical parameters abnormal");
        }

        score = Math.max(0, Math.min(100, score));

        String riskLevel;
        if (criticalCount > 0 || score >= 75) {
            riskLevel = Constants.RISK_CRITICAL;
        } else if (warningCount > 0 || score >= 35) {
            riskLevel = Constants.RISK_WARNING;
        } else {
            riskLevel = Constants.RISK_STABLE;
        }

        String summary;
        if (reasons.isEmpty()) {
            summary = "Vitals are currently within acceptable ICU thresholds.";
        } else {
            summary = buildSummary(reasons);
        }

        String alertType = deriveAlertType(reasons, riskLevel);
        return new EvaluationResult(riskLevel, score, summary, reasons, alertType);
    }

    public static EvaluationResult evaluate(VitalSign vitalSign) {
        if (vitalSign == null) {
            return new EvaluationResult(
                    Constants.RISK_STABLE,
                    0,
                    "No vitals available for evaluation.",
                    Collections.singletonList("No vital sign record found."),
                    "Prediction Alert"
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

    public static String deriveRiskLevel(
            int heartRate,
            int spo2,
            int systolicBp,
            int respiratoryRate,
            double temperature
    ) {
        return evaluate(heartRate, spo2, systolicBp, 0, respiratoryRate, temperature).getRiskLevel();
    }

    public static double deriveRiskScore(
            int heartRate,
            int spo2,
            int systolicBp,
            int respiratoryRate,
            double temperature
    ) {
        return evaluate(heartRate, spo2, systolicBp, 0, respiratoryRate, temperature).getRiskScore();
    }

    public static String deriveAlertType(List<String> reasons, String riskLevel) {
        String combined = reasons == null ? "" : String.join(" ", reasons).toLowerCase(Locale.US);
        if (combined.contains("spo2")) {
            return "Low SpO2";
        }
        if (combined.contains("blood pressure")) {
            return "Low Blood Pressure";
        }
        if (combined.contains("heart")) {
            return "High Heart Rate";
        }
        if (combined.contains("tachypnea") || combined.contains("respiratory")) {
            return "High Respiratory Rate";
        }
        if (combined.contains("fever")) {
            return "High Temperature";
        }
        if (reasons != null && reasons.size() >= 2) {
            return "Multi-Parameter Risk Alert";
        }
        if (Constants.RISK_CRITICAL.equalsIgnoreCase(riskLevel)) {
            return "Critical Prediction Alert";
        }
        return "Prediction Alert";
    }

    private static double normalizeToCelsius(double temperatureInput) {
        if (temperatureInput <= 0d) {
            return 0d;
        }

        // Inputs above a plausible Celsius ICU range are treated as Fahrenheit for safety.
        if (temperatureInput > 45d) {
            return (temperatureInput - 32d) * 5d / 9d;
        }
        return temperatureInput;
    }

    private static String buildSummary(List<String> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            return "Vitals are currently within acceptable ICU thresholds.";
        }
        if (reasons.size() == 1) {
            return reasons.get(0) + ".";
        }
        if (reasons.size() == 2) {
            return reasons.get(0) + " and " + reasons.get(1) + ".";
        }
        int additionalCount = reasons.size() - 2;
        return reasons.get(0) + ", " + reasons.get(1) + ", and "
                + additionalCount + " additional abnormal parameter(s).";
    }

    public static final class EvaluationResult {
        private final String riskLevel;
        private final double riskScore;
        private final String summary;
        private final List<String> reasons;
        private final String alertType;

        public EvaluationResult(String riskLevel, double riskScore, String summary, List<String> reasons, String alertType) {
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.summary = summary;
            this.reasons = reasons == null ? new ArrayList<>() : new ArrayList<>(reasons);
            this.alertType = alertType;
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

        public List<String> getReasons() {
            return new ArrayList<>(reasons);
        }

        public String getAlertType() {
            return alertType;
        }
    }
}
