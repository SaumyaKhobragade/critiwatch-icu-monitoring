package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.AlertDao;
import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertRepository {

    private final AlertDao alertDao;

    public AlertRepository(Context context) {
        this.alertDao = new AlertDao(context);
    }

    public long addAlert(AlertItem alertItem) {
        return alertDao.insertAlert(alertItem);
    }

    public long addAlertIfNotRecentDuplicate(AlertItem alertItem, int dedupeWindowMinutes) {
        if (isRecentDuplicate(alertItem, dedupeWindowMinutes)) {
            return -1L;
        }
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

    public AlertItem getLatestAlertByPatientId(int patientId) {
        return alertDao.getLatestAlertByPatientId(patientId);
    }

    public AlertItem getLatestUnacknowledgedAlertByType(int patientId, String alertType, String severity) {
        return alertDao.getLatestUnacknowledgedAlertByType(patientId, alertType, severity);
    }

    public boolean acknowledgeAlert(int alertId) {
        return alertDao.acknowledgeAlert(alertId) > 0;
    }

    public boolean deleteAlert(int alertId) {
        return alertDao.deleteAlert(alertId) > 0;
    }

    private boolean isRecentDuplicate(AlertItem incoming, int dedupeWindowMinutes) {
        if (incoming == null || dedupeWindowMinutes <= 0) {
            return false;
        }

        int patientId = parseId(incoming.getPatientId());
        if (patientId <= 0) {
            return false;
        }

        AlertItem latestMatch = alertDao.getLatestUnacknowledgedAlertByType(
                patientId,
                incoming.getType(),
                incoming.getSeverity()
        );
        if (latestMatch == null) {
            return false;
        }

        if (!sameText(latestMatch.getDescription(), incoming.getDescription())) {
            return false;
        }
        if (!sameText(latestMatch.getValue(), incoming.getValue())) {
            return false;
        }
        if (!sameText(latestMatch.getUnit(), incoming.getUnit())) {
            return false;
        }

        Date previous = DateTimeUtils.parse(latestMatch.getTimestamp());
        Date current = DateTimeUtils.parse(incoming.getTimestamp());
        if (previous == null || current == null) {
            return false;
        }

        long diffMillis = current.getTime() - previous.getTime();
        if (diffMillis < 0L) {
            diffMillis = Math.abs(diffMillis);
        }
        long windowMillis = dedupeWindowMinutes * 60L * 1000L;
        return diffMillis <= windowMillis;
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

    private boolean sameText(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.US);
    }
}
