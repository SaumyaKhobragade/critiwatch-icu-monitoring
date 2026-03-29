package com.example.critiwatch.models;

import java.util.Locale;

public class VitalSign {
    private final String timestamp;
    private final int heartRate;
    private final int spo2;
    private final String bloodPressure;
    private final int respiratoryRate;
    private final double temperature;

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
        this.bloodPressure = bloodPressure;
        this.respiratoryRate = respiratoryRate;
        this.temperature = temperature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getSpo2() {
        return spo2;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public int getRespiratoryRate() {
        return respiratoryRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getTemperatureText() {
        return String.format(Locale.US, "%.1f", temperature);
    }
}
