package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.AlertDao;
import com.example.critiwatch.models.AlertItem;

import java.util.List;

public class AlertRepository {

    private final AlertDao alertDao;

    public AlertRepository(Context context) {
        this.alertDao = new AlertDao(context);
    }

    public long addAlert(AlertItem alertItem) {
        return alertDao.insertAlert(alertItem);
    }

    public List<AlertItem> getAllAlerts() {
        return alertDao.getAllAlerts();
    }

    public List<AlertItem> getAlertsByPatientId(int patientId) {
        return alertDao.getAlertsByPatientId(patientId);
    }

    public AlertItem getAlertById(int alertId) {
        return alertDao.getAlertById(alertId);
    }

    public boolean acknowledgeAlert(int alertId) {
        return alertDao.acknowledgeAlert(alertId) > 0;
    }

    public boolean deleteAlert(int alertId) {
        return alertDao.deleteAlert(alertId) > 0;
    }
}
