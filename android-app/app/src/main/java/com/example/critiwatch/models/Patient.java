package com.example.critiwatch.models;

import java.util.Locale;

public class Patient {
    private String id;
    private String name;
    private int age;
    private String sex;
    private String ward;
    private String bedNumber;
    private String riskLevel;
    private String createdAt;
    private int heartRate;
    private int spo2;
    private String bloodPressure;
    private int respiratoryRate;
    private double temperature;
    private String lastUpdated;

    public Patient() {
    }

    public Patient(
            String id,
            String name,
            int age,
            String sex,
            String ward,
            String bedNumber,
            String riskLevel,
            String createdAt
    ) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.ward = ward;
        this.bedNumber = bedNumber;
        this.riskLevel = riskLevel;
        this.createdAt = createdAt;
        this.lastUpdated = createdAt;
    }

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
        this.ward = "";
        this.bedNumber = bedNumber;
        this.riskLevel = riskLevel;
        this.heartRate = heartRate;
        this.spo2 = spo2;
        this.bloodPressure = bloodPressure;
        this.respiratoryRate = respiratoryRate;
        this.temperature = temperature;
        this.lastUpdated = lastUpdated;
        this.createdAt = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getMetaLine() {
        String safeBed = bedNumber == null ? "-" : bedNumber;
        String safeSex = sex == null ? "-" : sex;
        return age + "y • " + safeSex + " • Bed " + safeBed;
    }

    public String getTemperatureDisplay() {
        return String.format(Locale.US, "%.1f°C", temperature);
    }
}
