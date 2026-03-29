package com.example.critiwatch.models;

public class AlertItem {
    private final String id;
    private final String type;
    private final String severity;
    private final String value;
    private final String unit;
    private final String timestamp;
    private final String description;
    private final String patientId;
    private final String patientName;
    private final int patientAge;
    private final String patientSex;
    private final String patientBed;
    private final String patientRisk;
    private final int predictionConfidence;

    public AlertItem(
            String id,
            String type,
            String severity,
            String value,
            String unit,
            String timestamp,
            String description,
            String patientId,
            String patientName,
            int patientAge,
            String patientSex,
            String patientBed,
            String patientRisk,
            int predictionConfidence
    ) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
        this.description = description;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientSex = patientSex;
        this.patientBed = patientBed;
        this.patientRisk = patientRisk;
        this.predictionConfidence = predictionConfidence;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public String getPatientBed() {
        return patientBed;
    }

    public String getPatientRisk() {
        return patientRisk;
    }

    public int getPredictionConfidence() {
        return predictionConfidence;
    }

    public String getPatientLabel() {
        return "Patient ID: " + patientId + " • " + patientName;
    }

    public String getSubtitleForNotification() {
        return type + " • " + severity;
    }
}
