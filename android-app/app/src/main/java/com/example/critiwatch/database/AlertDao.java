package com.example.critiwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class AlertDao {

    private final DatabaseHelper databaseHelper;

    public AlertDao(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public long insertAlert(AlertItem alert) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ALERT_PATIENT_ID, parseId(alert.getPatientId()));
        values.put(DatabaseHelper.COL_ALERT_TYPE, alert.getType());
        values.put(DatabaseHelper.COL_ALERT_SEVERITY, alert.getSeverity());
        values.put(DatabaseHelper.COL_ALERT_MESSAGE, alert.getDescription());
        values.put(DatabaseHelper.COL_ALERT_TIMESTAMP,
                alert.getTimestamp() == null || alert.getTimestamp().trim().isEmpty()
                        ? DateTimeUtils.now()
                        : alert.getTimestamp());
        values.put(DatabaseHelper.COL_ALERT_VALUE, alert.getValue());
        values.put(DatabaseHelper.COL_ALERT_UNIT, alert.getUnit());
        values.put(DatabaseHelper.COL_ALERT_PREDICTION_CONFIDENCE, alert.getPredictionConfidence());
        values.put(DatabaseHelper.COL_ALERT_ACKNOWLEDGED, alert.isAcknowledged() ? 1 : 0);
        return db.insert(DatabaseHelper.TABLE_ALERTS, null, values);
    }

    public List<AlertItem> getAllAlerts() {
        return runJoinedAlertQuery(
                "SELECT a.*, p.name AS p_name, p.age AS p_age, p.sex AS p_sex, "
                        + "p.bed_number AS p_bed, p.risk_level AS p_risk "
                        + "FROM " + DatabaseHelper.TABLE_ALERTS + " a "
                        + "LEFT JOIN " + DatabaseHelper.TABLE_PATIENTS + " p ON a."
                        + DatabaseHelper.COL_ALERT_PATIENT_ID + " = p." + DatabaseHelper.COL_ID + " "
                        + "ORDER BY a." + DatabaseHelper.COL_ALERT_TIMESTAMP + " DESC",
                null
        );
    }

    public List<AlertItem> getAlertsByPatientId(int patientId) {
        return runJoinedAlertQuery(
                "SELECT a.*, p.name AS p_name, p.age AS p_age, p.sex AS p_sex, "
                        + "p.bed_number AS p_bed, p.risk_level AS p_risk "
                        + "FROM " + DatabaseHelper.TABLE_ALERTS + " a "
                        + "LEFT JOIN " + DatabaseHelper.TABLE_PATIENTS + " p ON a."
                        + DatabaseHelper.COL_ALERT_PATIENT_ID + " = p." + DatabaseHelper.COL_ID + " "
                        + "WHERE a." + DatabaseHelper.COL_ALERT_PATIENT_ID + " = ? "
                        + "ORDER BY a." + DatabaseHelper.COL_ALERT_TIMESTAMP + " DESC",
                new String[]{String.valueOf(patientId)}
        );
    }

    public AlertItem getAlertById(int alertId) {
        List<AlertItem> alerts = runJoinedAlertQuery(
                "SELECT a.*, p.name AS p_name, p.age AS p_age, p.sex AS p_sex, "
                        + "p.bed_number AS p_bed, p.risk_level AS p_risk "
                        + "FROM " + DatabaseHelper.TABLE_ALERTS + " a "
                        + "LEFT JOIN " + DatabaseHelper.TABLE_PATIENTS + " p ON a."
                        + DatabaseHelper.COL_ALERT_PATIENT_ID + " = p." + DatabaseHelper.COL_ID + " "
                        + "WHERE a." + DatabaseHelper.COL_ID + " = ? LIMIT 1",
                new String[]{String.valueOf(alertId)}
        );
        return alerts.isEmpty() ? null : alerts.get(0);
    }

    public int acknowledgeAlert(int alertId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ALERT_ACKNOWLEDGED, 1);
        return db.update(
                DatabaseHelper.TABLE_ALERTS,
                values,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(alertId)}
        );
    }

    public int deleteAlert(int alertId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_ALERTS,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(alertId)}
        );
    }

    private List<AlertItem> runJoinedAlertQuery(String sql, String[] args) {
        List<AlertItem> alerts = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);

        try {
            while (cursor != null && cursor.moveToNext()) {
                alerts.add(mapCursorToAlert(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return alerts;
    }

    private AlertItem mapCursorToAlert(Cursor cursor) {
        int alertId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        int patientId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_PATIENT_ID));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_TYPE));
        String severity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_SEVERITY));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_MESSAGE));
        String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_TIMESTAMP));
        String value = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_VALUE));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_UNIT));
        int confidence = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_PREDICTION_CONFIDENCE));
        boolean acknowledged = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ALERT_ACKNOWLEDGED)) == 1;

        AlertItem alertItem = new AlertItem(
                String.valueOf(alertId),
                String.valueOf(patientId),
                type,
                severity,
                message,
                timestamp,
                value,
                unit,
                confidence,
                acknowledged
        );

        alertItem.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow("p_name")));
        alertItem.setPatientAge(cursor.getInt(cursor.getColumnIndexOrThrow("p_age")));
        alertItem.setPatientSex(cursor.getString(cursor.getColumnIndexOrThrow("p_sex")));
        alertItem.setPatientBed(cursor.getString(cursor.getColumnIndexOrThrow("p_bed")));
        alertItem.setPatientRisk(cursor.getString(cursor.getColumnIndexOrThrow("p_risk")));

        return alertItem;
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
