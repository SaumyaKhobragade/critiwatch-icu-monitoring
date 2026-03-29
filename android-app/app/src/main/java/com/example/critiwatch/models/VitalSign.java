package com.example.critiwatch.models;

import java.util.Locale;

public class VitalSign {
    private String id;
    private String patientId;
    private String timestamp;
    private int heartRate;
    private int spo2;
    private int systolicBp;
    private int diastolicBp;
    private String bloodPressure;
    private int respiratoryRate;
    private double temperature;
    private double heightCm;
    private double weightKg;

    public VitalSign() {
    }

    public VitalSign(
            String timestamp,
            int heartRate,
            int spo2,
            String bloodPressure,
            int respiratoryRate,
            double temperature
    ) {
        this.timestamp = timestamp;
        this.heartRate = heartRate;
        this.spo2 = spo2;
        this.systolicBp = parseSystolic(bloodPressure);
        this.diastolicBp = parseDiastolic(bloodPressure);
        this.bloodPressure = bloodPressure;
        this.respiratoryRate = respiratoryRate;
        this.temperature = temperature;
    }

    public VitalSign(
            String id,
            String patientId,
            int heartRate,
            int spo2,
            int systolicBp,
            int diastolicBp,
            int respiratoryRate,
            double temperature,
            double heightCm,
            double weightKg,
            String timestamp
    ) {
        this.id = id;
        this.patientId = patientId;
        this.heartRate = heartRate;
        this.spo2 = spo2;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.respiratoryRate = respiratoryRate;
        this.temperature = temperature;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.timestamp = timestamp;
        this.bloodPressure = systolicBp + "/" + diastolicBp;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getSpo2() {
        return spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public int getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(int systolicBp) {
        this.systolicBp = systolicBp;
        this.bloodPressure = systolicBp + "/" + diastolicBp;
    }

    public int getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(int diastolicBp) {
        this.diastolicBp = diastolicBp;
        this.bloodPressure = systolicBp + "/" + diastolicBp;
    }

    public String getBloodPressure() {
        if (bloodPressure == null || bloodPressure.trim().isEmpty()) {
            bloodPressure = systolicBp + "/" + diastolicBp;
        }
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
        this.systolicBp = parseSystolic(bloodPressure);
        this.diastolicBp = parseDiastolic(bloodPressure);
    }

    public int getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(int respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public String getTemperatureText() {
        return String.format(Locale.US, "%.1f", temperature);
    }

    private int parseSystolic(String bp) {
        if (bp == null || !bp.contains("/")) {
            return 0;
        }
        try {
            return Integer.parseInt(bp.split("/")[0].trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private int parseDiastolic(String bp) {
        if (bp == null || !bp.contains("/")) {
            return 0;
        }
        try {
            return Integer.parseInt(bp.split("/")[1].trim());
        } catch (Exception ignored) {
            return 0;
        }
    }
}
