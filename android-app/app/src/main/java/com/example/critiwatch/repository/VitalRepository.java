package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.VitalDao;
import com.example.critiwatch.models.VitalSign;

import java.util.List;

public class VitalRepository {

    private final VitalDao vitalDao;

    public VitalRepository(Context context) {
        this.vitalDao = new VitalDao(context);
    }

    public long addVital(VitalSign vitalSign) {
        return vitalDao.insertVital(vitalSign);
    }

    public List<VitalSign> getVitalsByPatientId(int patientId) {
        return vitalDao.getVitalsByPatientId(patientId);
    }

    public VitalSign getLatestVitalByPatientId(int patientId) {
        return vitalDao.getLatestVitalByPatientId(patientId);
    }

    public boolean deleteVitalsByPatientId(int patientId) {
        return vitalDao.deleteVitalsByPatientId(patientId) > 0;
    }
}
