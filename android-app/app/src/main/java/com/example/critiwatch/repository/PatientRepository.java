package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.PatientDao;
import com.example.critiwatch.database.PredictionDao;
import com.example.critiwatch.database.VitalDao;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.Prediction;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

    private final PatientDao patientDao;
    private final VitalDao vitalDao;
    private final PredictionDao predictionDao;

    public PatientRepository(Context context) {
        patientDao = new PatientDao(context);
        vitalDao = new VitalDao(context);
        predictionDao = new PredictionDao(context);
    }

    public long addPatient(Patient patient, VitalSign initialVital, Prediction initialPrediction) {
        long patientRowId = patientDao.insertPatient(patient);
        if (patientRowId <= 0) {
            return patientRowId;
        }

        String patientId = String.valueOf(patientRowId);
        if (initialVital != null) {
            initialVital.setPatientId(patientId);
            vitalDao.insertVital(initialVital);
        }

        if (initialPrediction != null) {
            initialPrediction.setPatientId(patientId);
            predictionDao.insertPrediction(initialPrediction);
        }

        return patientRowId;
    }

    public List<Patient> getAllPatientsWithLatestData() {
        List<Patient> patients = patientDao.getAllPatients();
        List<Patient> hydrated = new ArrayList<>();
        for (Patient patient : patients) {
            hydrated.add(attachLatestData(patient));
        }
        return hydrated;
    }

    public Patient getPatientByIdWithLatestData(int patientId) {
        Patient patient = patientDao.getPatientById(patientId);
        if (patient == null) {
            return null;
        }
        return attachLatestData(patient);
    }

    public boolean isEmpty() {
        return patientDao.getAllPatients().isEmpty();
    }

    public boolean deletePatient(int patientId) {
        return patientDao.deletePatient(patientId) > 0;
    }

    private Patient attachLatestData(Patient patient) {
        int id = parseId(patient.getId());
        if (id <= 0) {
            return patient;
        }

        VitalSign latestVital = vitalDao.getLatestVitalByPatientId(id);
        if (latestVital != null) {
            patient.setHeartRate(latestVital.getHeartRate());
            patient.setSpo2(latestVital.getSpo2());
            patient.setBloodPressure(latestVital.getBloodPressure());
            patient.setRespiratoryRate(latestVital.getRespiratoryRate());
            patient.setTemperature(latestVital.getTemperature());
            patient.setLastUpdated(DateTimeUtils.toRelativeTime(latestVital.getTimestamp()));
        } else {
            patient.setLastUpdated(DateTimeUtils.toRelativeTime(patient.getCreatedAt()));
        }

        Prediction latestPrediction = predictionDao.getLatestPredictionByPatientId(id);
        if (latestPrediction != null && latestPrediction.getRiskLevel() != null) {
            patient.setRiskLevel(latestPrediction.getRiskLevel());
        }

        return patient;
    }

    private int parseId(String rawId) {
        if (rawId == null || rawId.trim().isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(rawId.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}
