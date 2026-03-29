package com.example.critiwatch.models;

public class Prediction {
    private String id;
    private String patientId;
    private String riskLevel;
    private double riskScore;
    private String summary;
    private String createdAt;

    public Prediction() {
    }

    public Prediction(String riskLabel, int confidencePercent, String summary, String window) {
        this.riskLevel = riskLabel;
        this.riskScore = confidencePercent;
        this.summary = summary;
        this.createdAt = window;
    }

    public Prediction(
            String id,
            String patientId,
            String riskLevel,
            double riskScore,
            String summary,
            String createdAt
    ) {
        this.id = id;
        this.patientId = patientId;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRiskLabel() {
        return riskLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getConfidencePercent() {
        return (int) Math.round(riskScore);
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getWindow() {
        return createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getConfidenceText() {
        return getConfidencePercent() + "%";
    }
}
