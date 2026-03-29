package com.example.critiwatch.utils;

public class Constants {

    private Constants() {
        // Utility class
    }

    // Intent extras
    public static final String EXTRA_PATIENT_ID = "patient_id";
    public static final String EXTRA_PATIENT_NAME = "patient_name";
    public static final String EXTRA_PATIENT_AGE = "patient_age";
    public static final String EXTRA_PATIENT_SEX = "patient_sex";
    public static final String EXTRA_PATIENT_BED = "patient_bed";
    public static final String EXTRA_PATIENT_RISK = "patient_risk";
    public static final String EXTRA_PATIENT_HEART_RATE = "patient_heart_rate";
    public static final String EXTRA_PATIENT_SPO2 = "patient_spo2";
    public static final String EXTRA_PATIENT_BP = "patient_bp";
    public static final String EXTRA_PATIENT_RR = "patient_rr";
    public static final String EXTRA_PATIENT_TEMP = "patient_temp";

    public static final String EXTRA_ALERT_ID = "alert_id";
    public static final String EXTRA_ALERT_TYPE = "alert_type";
    public static final String EXTRA_ALERT_SEVERITY = "alert_severity";
    public static final String EXTRA_ALERT_VALUE = "alert_value";
    public static final String EXTRA_ALERT_UNIT = "alert_unit";
    public static final String EXTRA_ALERT_TIMESTAMP = "alert_timestamp";
    public static final String EXTRA_ALERT_DESCRIPTION = "alert_description";
    public static final String EXTRA_PREDICTION_CONFIDENCE = "prediction_confidence";

    // Risk labels
    public static final String RISK_CRITICAL = "Critical";
    public static final String RISK_WARNING = "Warning";
    public static final String RISK_STABLE = "Stable";
}
