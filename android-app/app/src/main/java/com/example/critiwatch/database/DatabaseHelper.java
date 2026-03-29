package com.example.critiwatch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "critiwatch_local.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_PATIENTS = "patients";
    public static final String TABLE_VITAL_SIGNS = "vital_signs";
    public static final String TABLE_PREDICTIONS = "predictions";
    public static final String TABLE_ALERTS = "alerts";

    public static final String COL_ID = "id";

    public static final String COL_PATIENT_NAME = "name";
    public static final String COL_PATIENT_AGE = "age";
    public static final String COL_PATIENT_SEX = "sex";
    public static final String COL_PATIENT_WARD = "ward";
    public static final String COL_PATIENT_BED_NUMBER = "bed_number";
    public static final String COL_PATIENT_RISK_LEVEL = "risk_level";
    public static final String COL_PATIENT_CREATED_AT = "created_at";

    public static final String COL_VITAL_PATIENT_ID = "patient_id";
    public static final String COL_VITAL_HEART_RATE = "heart_rate";
    public static final String COL_VITAL_SPO2 = "spo2";
    public static final String COL_VITAL_SYSTOLIC_BP = "systolic_bp";
    public static final String COL_VITAL_DIASTOLIC_BP = "diastolic_bp";
    public static final String COL_VITAL_RESP_RATE = "respiratory_rate";
    public static final String COL_VITAL_TEMPERATURE = "temperature";
    public static final String COL_VITAL_HEIGHT_CM = "height_cm";
    public static final String COL_VITAL_WEIGHT_KG = "weight_kg";
    public static final String COL_VITAL_RECORDED_AT = "recorded_at";

    public static final String COL_PREDICTION_PATIENT_ID = "patient_id";
    public static final String COL_PREDICTION_RISK_LEVEL = "risk_level";
    public static final String COL_PREDICTION_RISK_SCORE = "risk_score";
    public static final String COL_PREDICTION_SUMMARY = "summary";
    public static final String COL_PREDICTION_CREATED_AT = "created_at";

    public static final String COL_ALERT_PATIENT_ID = "patient_id";
    public static final String COL_ALERT_TYPE = "alert_type";
    public static final String COL_ALERT_SEVERITY = "severity";
    public static final String COL_ALERT_MESSAGE = "message";
    public static final String COL_ALERT_TIMESTAMP = "timestamp";
    public static final String COL_ALERT_VALUE = "value";
    public static final String COL_ALERT_UNIT = "unit";
    public static final String COL_ALERT_PREDICTION_CONFIDENCE = "prediction_confidence";
    public static final String COL_ALERT_ACKNOWLEDGED = "acknowledged";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPatientsTable = "CREATE TABLE " + TABLE_PATIENTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PATIENT_NAME + " TEXT NOT NULL, "
                + COL_PATIENT_AGE + " INTEGER, "
                + COL_PATIENT_SEX + " TEXT, "
                + COL_PATIENT_WARD + " TEXT, "
                + COL_PATIENT_BED_NUMBER + " TEXT, "
                + COL_PATIENT_RISK_LEVEL + " TEXT, "
                + COL_PATIENT_CREATED_AT + " TEXT"
                + ");";

        String createVitalSignsTable = "CREATE TABLE " + TABLE_VITAL_SIGNS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_VITAL_PATIENT_ID + " INTEGER NOT NULL, "
                + COL_VITAL_HEART_RATE + " INTEGER, "
                + COL_VITAL_SPO2 + " INTEGER, "
                + COL_VITAL_SYSTOLIC_BP + " INTEGER, "
                + COL_VITAL_DIASTOLIC_BP + " INTEGER, "
                + COL_VITAL_RESP_RATE + " INTEGER, "
                + COL_VITAL_TEMPERATURE + " REAL, "
                + COL_VITAL_HEIGHT_CM + " REAL, "
                + COL_VITAL_WEIGHT_KG + " REAL, "
                + COL_VITAL_RECORDED_AT + " TEXT, "
                + "FOREIGN KEY(" + COL_VITAL_PATIENT_ID + ") REFERENCES " + TABLE_PATIENTS + "(" + COL_ID + ") ON DELETE CASCADE"
                + ");";

        String createPredictionsTable = "CREATE TABLE " + TABLE_PREDICTIONS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PREDICTION_PATIENT_ID + " INTEGER NOT NULL, "
                + COL_PREDICTION_RISK_LEVEL + " TEXT, "
                + COL_PREDICTION_RISK_SCORE + " REAL, "
                + COL_PREDICTION_SUMMARY + " TEXT, "
                + COL_PREDICTION_CREATED_AT + " TEXT, "
                + "FOREIGN KEY(" + COL_PREDICTION_PATIENT_ID + ") REFERENCES " + TABLE_PATIENTS + "(" + COL_ID + ") ON DELETE CASCADE"
                + ");";

        String createAlertsTable = "CREATE TABLE " + TABLE_ALERTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ALERT_PATIENT_ID + " INTEGER, "
                + COL_ALERT_TYPE + " TEXT, "
                + COL_ALERT_SEVERITY + " TEXT, "
                + COL_ALERT_MESSAGE + " TEXT, "
                + COL_ALERT_TIMESTAMP + " TEXT, "
                + COL_ALERT_VALUE + " TEXT, "
                + COL_ALERT_UNIT + " TEXT, "
                + COL_ALERT_PREDICTION_CONFIDENCE + " INTEGER DEFAULT 0, "
                + COL_ALERT_ACKNOWLEDGED + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COL_ALERT_PATIENT_ID + ") REFERENCES " + TABLE_PATIENTS + "(" + COL_ID + ") ON DELETE CASCADE"
                + ");";

        db.execSQL(createPatientsTable);
        db.execSQL(createVitalSignsTable);
        db.execSQL(createPredictionsTable);
        db.execSQL(createAlertsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREDICTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VITAL_SIGNS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
        onCreate(db);
    }
}
