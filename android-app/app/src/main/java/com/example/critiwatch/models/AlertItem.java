package com.example.critiwatch.models;

public class AlertItem {
    private String id;
    private String type;
    private String severity;
    private String value;
    private String unit;
    private String timestamp;
    private String description;
    private String patientId;
    private String patientName;
    private int patientAge;
    private String patientSex;
    private String patientBed;
    private String patientRisk;
    private int predictionConfidence;
    private boolean acknowledged;

    public AlertItem() {
    }

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
        this.acknowledged = false;
    }

    public AlertItem(
            String id,
            String patientId,
            String type,
            String severity,
            String description,
            String timestamp,
            String value,
            String unit,
            int predictionConfidence,
            boolean acknowledged
    ) {
        this.id = id;
        this.patientId = patientId;
        this.type = type;
        this.severity = severity;
        this.description = description;
        this.timestamp = timestamp;
        this.value = value;
        this.unit = unit;
        this.predictionConfidence = predictionConfidence;
        this.acknowledged = acknowledged;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getPatientBed() {
        return patientBed;
    }

    public void setPatientBed(String patientBed) {
        this.patientBed = patientBed;
    }

    public String getPatientRisk() {
        return patientRisk;
    }

    public void setPatientRisk(String patientRisk) {
        this.patientRisk = patientRisk;
    }

    public int getPredictionConfidence() {
        return predictionConfidence;
    }

    public void setPredictionConfidence(int predictionConfidence) {
        this.predictionConfidence = predictionConfidence;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public String getPatientLabel() {
        String safeId = patientId == null ? "-" : patientId;
        String safeName = patientName == null ? "Unknown Patient" : patientName;
        return "Patient ID: " + safeId + " • " + safeName;
    }

    public String getSubtitleForNotification() {
        return type + " • " + severity;
    }
}
