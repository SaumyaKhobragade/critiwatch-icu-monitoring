package com.example.critiwatch.models;

import java.util.Locale;

public class Patient {
    private final String id;
    private final String name;
    private final int age;
    private final String sex;
    private final String bedNumber;
    private final String riskLevel;
    private final int heartRate;
    private final int spo2;
    private final String bloodPressure;
    private final int respiratoryRate;
    private final double temperature;
    private final String lastUpdated;

    public Patient(
            String id,
            String name,
            int age,
            String sex,
            String bedNumber,
            String riskLevel,
            int heartRate,
            int spo2,
            String bloodPressure,
            int respiratoryRate,
            double temperature,
            String lastUpdated
    ) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.bedNumber = bedNumber;
        this.riskLevel = riskLevel;
        this.heartRate = heartRate;
        this.spo2 = spo2;
        this.bloodPressure = bloodPressure;
        this.respiratoryRate = respiratoryRate;
        this.temperature = temperature;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public String getRiskLevel() {
        return riskLevel;
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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getMetaLine() {
        return age + "y • " + sex + " • Bed " + bedNumber;
    }

    public String getTemperatureDisplay() {
        return String.format(Locale.US, "%.1f°C", temperature);
    }
}
