package com.example.critiwatch.models;

public class Prediction {
    private final String riskLabel;
    private final int confidencePercent;
    private final String summary;
    private final String window;

    public Prediction(String riskLabel, int confidencePercent, String summary, String window) {
        this.riskLabel = riskLabel;
        this.confidencePercent = confidencePercent;
        this.summary = summary;
        this.window = window;
    }

    public String getRiskLabel() {
        return riskLabel;
    }

    public int getConfidencePercent() {
        return confidencePercent;
    }

    public String getSummary() {
        return summary;
    }

    public String getWindow() {
        return window;
    }

    public String getConfidenceText() {
        return confidencePercent + "%";
    }
}
