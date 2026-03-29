package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.SystemUiUtils;

import java.util.Locale;

public class NotificationDetailActivity extends AppCompatActivity {

    private String patientId;
    private String patientName;
    private int patientAge;
    private String patientSex;
    private String patientBed;
    private String patientRisk;

    private String alertId;
    private String alertType;
    private String alertSeverity;
    private String alertValue;
    private String alertUnit;
    private String alertTimestamp;
    private String alertDescription;
    private int predictionConfidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_detail);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readIntentData();
        bindAlertHeader();
        bindPatientSummary();
        bindAlertParameters();
        setupActions();
    }

    private void readIntentData() {
        Intent source = getIntent();
        patientId = source.getStringExtra(Constants.EXTRA_PATIENT_ID);
        patientName = source.getStringExtra(Constants.EXTRA_PATIENT_NAME);
        patientAge = source.getIntExtra(Constants.EXTRA_PATIENT_AGE, 64);
        patientSex = source.getStringExtra(Constants.EXTRA_PATIENT_SEX);
        patientBed = source.getStringExtra(Constants.EXTRA_PATIENT_BED);
        patientRisk = source.getStringExtra(Constants.EXTRA_PATIENT_RISK);

        alertId = source.getStringExtra(Constants.EXTRA_ALERT_ID);
        alertType = source.getStringExtra(Constants.EXTRA_ALERT_TYPE);
        alertSeverity = source.getStringExtra(Constants.EXTRA_ALERT_SEVERITY);
        alertValue = source.getStringExtra(Constants.EXTRA_ALERT_VALUE);
        alertUnit = source.getStringExtra(Constants.EXTRA_ALERT_UNIT);
        alertTimestamp = source.getStringExtra(Constants.EXTRA_ALERT_TIMESTAMP);
        alertDescription = source.getStringExtra(Constants.EXTRA_ALERT_DESCRIPTION);
        predictionConfidence = source.getIntExtra(Constants.EXTRA_PREDICTION_CONFIDENCE, 88);

        if (patientId == null || patientId.trim().isEmpty()) {
            patientId = "P102";
        }
        if (patientName == null || patientName.trim().isEmpty()) {
            patientName = "Sarah Johnson";
        }
        if (patientSex == null || patientSex.trim().isEmpty()) {
            patientSex = "Female";
        }
        if (patientBed == null || patientBed.trim().isEmpty()) {
            patientBed = "ICU-04";
        }
        if (patientRisk == null || patientRisk.trim().isEmpty()) {
            patientRisk = Constants.RISK_WARNING;
        }

        if (alertId == null || alertId.trim().isEmpty()) {
            alertId = "ALT-001";
        }
        if (alertType == null || alertType.trim().isEmpty()) {
            alertType = "Prediction Alert";
        }
        if (alertSeverity == null || alertSeverity.trim().isEmpty()) {
            alertSeverity = patientRisk;
        }
        if (alertValue == null || alertValue.trim().isEmpty()) {
            alertValue = "132";
        }
        if (alertUnit == null || alertUnit.trim().isEmpty()) {
            alertUnit = "BPM";
        }
        if (alertTimestamp == null || alertTimestamp.trim().isEmpty()) {
            alertTimestamp = "Just now";
        }
        if (alertDescription == null || alertDescription.trim().isEmpty()) {
            alertDescription = "Significant deterioration trend observed. Immediate bedside review advised.";
        }
    }

    private void bindAlertHeader() {
        TextView tvAlertTitle = findViewById(R.id.tvAlertTitle);
        TextView tvAlertSubtitle = findViewById(R.id.tvAlertSubtitle);
        TextView tvAlertDescription = findViewById(R.id.tvAlertDescription);
        LinearLayout llAlertHeader = findViewById(R.id.llAlertHeader);

        if (tvAlertTitle != null) {
            tvAlertTitle.setText(alertSeverity.toUpperCase(Locale.US) + " ALERT");
            tvAlertTitle.setTextColor(ContextCompat.getColor(this, getSeverityColor(alertSeverity)));
        }
        if (tvAlertSubtitle != null) {
            tvAlertSubtitle.setText(alertType + " • " + alertTimestamp + " • " + alertId);
        }
        if (tvAlertDescription != null) {
            tvAlertDescription.setText(alertDescription);
        }
        if (llAlertHeader != null) {
            if (Constants.RISK_CRITICAL.equalsIgnoreCase(alertSeverity)) {
                llAlertHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.soft_red));
            } else if (Constants.RISK_WARNING.equalsIgnoreCase(alertSeverity)) {
                llAlertHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.soft_amber));
            } else {
                llAlertHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.soft_green));
            }
        }
    }

    private void bindPatientSummary() {
        TextView tvPatientNameLarge = findViewById(R.id.tvPatientNameLarge);
        TextView tvPatientMetaLarge = findViewById(R.id.tvPatientMetaLarge);

        if (tvPatientNameLarge != null) {
            tvPatientNameLarge.setText(patientName);
        }
        if (tvPatientMetaLarge != null) {
            tvPatientMetaLarge.setText("Bed " + patientBed + " • " + patientSex + " • " + patientAge + "y");
        }
    }

    private void bindAlertParameters() {
        TextView tvAlertHRValue = findViewById(R.id.tvAlertHRValue);
        TextView tvAlertSpO2Value = findViewById(R.id.tvAlertSpO2Value);
        TextView tvAlertBPValue = findViewById(R.id.tvAlertBPValue);
        TextView tvAlertConfidenceValue = findViewById(R.id.tvAlertConfidenceValue);

        int severityColor = ContextCompat.getColor(this, getSeverityColor(alertSeverity));
        if (tvAlertHRValue != null) {
            tvAlertHRValue.setText(alertType.toLowerCase(Locale.US).contains("heart")
                    ? alertValue + " " + alertUnit
                    : "126 BPM");
            tvAlertHRValue.setTextColor(severityColor);
        }
        if (tvAlertSpO2Value != null) {
            tvAlertSpO2Value.setText(alertType.toLowerCase(Locale.US).contains("spo2")
                    ? alertValue + alertUnit
                    : "86%");
            tvAlertSpO2Value.setTextColor(severityColor);
        }
        if (tvAlertBPValue != null) {
            tvAlertBPValue.setText(alertType.toLowerCase(Locale.US).contains("blood pressure")
                    ? alertValue + " " + alertUnit
                    : "85/55 mmHg");
            tvAlertBPValue.setTextColor(severityColor);
        }
        if (tvAlertConfidenceValue != null) {
            tvAlertConfidenceValue.setText(predictionConfidence + "%");
        }
    }

    private void setupActions() {
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        Button btnViewPatientProfile = findViewById(R.id.btnViewPatientProfile);
        if (btnViewPatientProfile != null) {
            btnViewPatientProfile.setOnClickListener(v -> openPatientProfile());
        } else {
            Toast.makeText(this, "Missing view id: btnViewPatientProfile", Toast.LENGTH_LONG).show();
        }

        Button btnAcknowledgeAlert = findViewById(R.id.btnAcknowledgeAlert);
        if (btnAcknowledgeAlert != null) {
            btnAcknowledgeAlert.setOnClickListener(v -> {
                Toast.makeText(this, "Alert " + alertId + " acknowledged", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AlertsActivity.class));
            });
        }

        Button btnActivateResponse = findViewById(R.id.btnActivateResponse);
        if (btnActivateResponse != null) {
            btnActivateResponse.setOnClickListener(v ->
                    Toast.makeText(this, "Rapid response team notified (mock)", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void openPatientProfile() {
        Intent intent = new Intent(this, PatientDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PATIENT_ID, patientId);
        intent.putExtra(Constants.EXTRA_PATIENT_NAME, patientName);
        intent.putExtra(Constants.EXTRA_PATIENT_AGE, patientAge);
        intent.putExtra(Constants.EXTRA_PATIENT_SEX, patientSex);
        intent.putExtra(Constants.EXTRA_PATIENT_BED, patientBed);
        intent.putExtra(Constants.EXTRA_PATIENT_RISK, patientRisk);
        startActivity(intent);
    }

    private int getSeverityColor(String severity) {
        if (Constants.RISK_CRITICAL.equalsIgnoreCase(severity)) {
            return R.color.status_critical;
        }
        if (Constants.RISK_WARNING.equalsIgnoreCase(severity)) {
            return R.color.status_warning;
        }
        return R.color.status_stable;
    }
}
