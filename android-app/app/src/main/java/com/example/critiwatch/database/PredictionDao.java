package com.example.critiwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.critiwatch.models.Prediction;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class PredictionDao {

    private final DatabaseHelper databaseHelper;

    public PredictionDao(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public long insertPrediction(Prediction prediction) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PREDICTION_PATIENT_ID, parseId(prediction.getPatientId()));
        values.put(DatabaseHelper.COL_PREDICTION_RISK_LEVEL, prediction.getRiskLevel());
        values.put(DatabaseHelper.COL_PREDICTION_RISK_SCORE, prediction.getRiskScore());
        values.put(DatabaseHelper.COL_PREDICTION_SUMMARY, prediction.getSummary());
        values.put(DatabaseHelper.COL_PREDICTION_CREATED_AT,
                prediction.getCreatedAt() == null || prediction.getCreatedAt().trim().isEmpty()
                        ? DateTimeUtils.now()
                        : prediction.getCreatedAt());
        return db.insert(DatabaseHelper.TABLE_PREDICTIONS, null, values);
    }

    public List<Prediction> getPredictionsByPatientId(int patientId) {
        List<Prediction> predictions = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PREDICTIONS,
                null,
                DatabaseHelper.COL_PREDICTION_PATIENT_ID + "=?",
                new String[]{String.valueOf(patientId)},
                null,
                null,
                DatabaseHelper.COL_PREDICTION_CREATED_AT + " DESC"
        );

        try {
            while (cursor != null && cursor.moveToNext()) {
                predictions.add(mapCursorToPrediction(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return predictions;
    }

    public Prediction getLatestPredictionByPatientId(int patientId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PREDICTIONS,
                null,
                DatabaseHelper.COL_PREDICTION_PATIENT_ID + "=?",
                new String[]{String.valueOf(patientId)},
                null,
                null,
                DatabaseHelper.COL_PREDICTION_CREATED_AT + " DESC",
                "1"
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToPrediction(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    private Prediction mapCursorToPrediction(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        int patientId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PREDICTION_PATIENT_ID));
        String riskLevel = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PREDICTION_RISK_LEVEL));
        double riskScore = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PREDICTION_RISK_SCORE));
        String summary = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PREDICTION_SUMMARY));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PREDICTION_CREATED_AT));

        return new Prediction(
                String.valueOf(id),
                String.valueOf(patientId),
                riskLevel,
                riskScore,
                summary,
                createdAt
        );
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
