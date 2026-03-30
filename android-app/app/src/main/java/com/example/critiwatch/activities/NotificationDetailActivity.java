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

import com.example.critiwatch.database.DatabaseSeeder;
import com.example.critiwatch.database.VitalDao;
import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.repository.AlertRepository;
import com.example.critiwatch.repository.PatientRepository;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.DateTimeUtils;
import com.example.critiwatch.utils.SystemUiUtils;

import java.util.Locale;

public class NotificationDetailActivity extends AppCompatActivity {

    private AlertRepository alertRepository;
    private PatientRepository patientRepository;
    private VitalDao vitalDao;

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
    private boolean alertAcknowledged;
    private VitalSign latestVital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_detail);
        DatabaseSeeder.seedIfEmpty(this);
        alertRepository = new AlertRepository(this);
        patientRepository = new PatientRepository(this);
        vitalDao = new VitalDao(this);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!readAndLoadData()) {
            return;
        }

        bindAlertHeader();
        bindPatientSummary();
        bindAlertParameters();
        setupActions();
    }

    private boolean readAndLoadData() {
        Intent source = getIntent();
        alertId = source == null ? null : source.getStringExtra(Constants.EXTRA_ALERT_ID);
        if (alertId == null || alertId.trim().isEmpty()) {
            Toast.makeText(this, "Missing alert id", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        if (!loadAlertFromDatabase(parseId(alertId))) {
            Toast.makeText(this, "Alert not found", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        loadPatientFromDatabaseIfPossible();
        return true;
    }

    private boolean loadAlertFromDatabase(int id) {
        if (id <= 0) {
            return false;
        }

        AlertItem alert = alertRepository.getAlertById(id);
        if (alert == null) {
            return false;
        }

        alertId = alert.getId();
        alertType = safeOrDash(alert.getType());
        alertSeverity = safeOrDefault(alert.getSeverity(), Constants.RISK_WARNING);
        alertValue = safeOrDash(alert.getValue());
        alertUnit = safeOrEmpty(alert.getUnit());
        alertTimestamp = safeOrDash(alert.getTimestamp());
        alertDescription = safeOrDefault(alert.getDescription(), "No alert description available.");
        predictionConfidence = Math.max(0, alert.getPredictionConfidence());
        alertAcknowledged = alert.isAcknowledged();

        patientId = alert.getPatientId();
        patientName = safeOrEmpty(alert.getPatientName());
        patientAge = alert.getPatientAge();
        patientSex = safeOrEmpty(alert.getPatientSex());
        patientBed = safeOrEmpty(alert.getPatientBed());
        patientRisk = safeOrDefault(alert.getPatientRisk(), alertSeverity);
        return true;
    }

    private void loadPatientFromDatabaseIfPossible() {
        int id = parseId(patientId);
        if (id <= 0) {
            patientName = safeOrDefault(patientName, "Unknown Patient");
            patientSex = safeOrDefault(patientSex, "Unknown");
            patientBed = safeOrDefault(patientBed, "-");
            if (patientAge <= 0) {
                patientAge = 0;
            }
            return;
        }

        Patient patient = patientRepository.getPatientByIdWithLatestData(id);
        if (patient != null) {
            patientId = patient.getId();
            patientName = safeOrDefault(patient.getName(), patientName);
            patientAge = patient.getAge();
            patientSex = safeOrDefault(patient.getSex(), patientSex);
            patientBed = safeOrDefault(patient.getBedNumber(), patientBed);
            patientRisk = safeOrDefault(patient.getRiskLevel(), patientRisk);
        }

        latestVital = vitalDao.getLatestVitalByPatientId(id);
    }

    private void bindAlertHeader() {
        TextView tvAlertTitle = findViewById(R.id.tvAlertTitle);
        TextView tvAlertSubtitle = findViewById(R.id.tvAlertSubtitle);
        TextView tvAlertDescription = findViewById(R.id.tvAlertDescription);
        LinearLayout llAlertHeader = findViewById(R.id.llAlertHeader);

        if (tvAlertTitle != null) {
            String title = alertSeverity.toUpperCase(Locale.US) + " ALERT";
            if (alertAcknowledged) {
                title += " (ACKNOWLEDGED)";
            }
            tvAlertTitle.setText(title);
            tvAlertTitle.setTextColor(ContextCompat.getColor(this, alertAcknowledged
                    ? R.color.text_secondary
                    : getSeverityColor(alertSeverity)));
        }
        if (tvAlertSubtitle != null) {
            String subtitleTime = DateTimeUtils.toRelativeTime(alertTimestamp);
            tvAlertSubtitle.setText(alertType + " • " + subtitleTime + " • #" + alertId);
        }
        if (tvAlertDescription != null) {
            tvAlertDescription.setText(alertDescription);
        }
        if (llAlertHeader != null) {
            if (alertAcknowledged) {
                llAlertHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.surface_secondary));
            } else if (Constants.RISK_CRITICAL.equalsIgnoreCase(alertSeverity)) {
                llAlertHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.soft_red));
            } else if (Constants.RISK_WARNING.equalsIgnoreCase(alertSeverity)) {
                llAlertHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.soft_amber));
            } else {
                llAlertHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.soft_green));
            }
        }

        setTextIfPresent("tvAlertSeverity", alertSeverity);
        setTextIfPresent("tvAlertMessage", alertDescription);
        setTextIfPresent("tvAlertTimestamp", DateTimeUtils.toRelativeTime(alertTimestamp));
        setTextIfPresent("tvPatientName", safeOrDefault(patientName, "Unknown Patient"));
    }

    private void bindPatientSummary() {
        TextView tvPatientNameLarge = findViewById(R.id.tvPatientNameLarge);
        TextView tvPatientMetaLarge = findViewById(R.id.tvPatientMetaLarge);

        if (tvPatientNameLarge != null) {
            tvPatientNameLarge.setText(safeOrDefault(patientName, "Unknown Patient"));
        }
        if (tvPatientMetaLarge != null) {
            String ageText = patientAge > 0 ? patientAge + "y" : "Age N/A";
            tvPatientMetaLarge.setText("Bed " + safeOrDefault(patientBed, "-") + " • "
                    + safeOrDefault(patientSex, "Unknown") + " • " + ageText);
        }
    }

    private void bindAlertParameters() {
        TextView tvAlertHRValue = findViewById(R.id.tvAlertHRValue);
        TextView tvAlertSpO2Value = findViewById(R.id.tvAlertSpO2Value);
        TextView tvAlertBPValue = findViewById(R.id.tvAlertBPValue);
        TextView tvAlertConfidenceValue = findViewById(R.id.tvAlertConfidenceValue);

        String hrText = latestVital != null && latestVital.getHeartRate() > 0
                ? latestVital.getHeartRate() + " BPM"
                : "--";
        String spo2Text = latestVital != null && latestVital.getSpo2() > 0
                ? latestVital.getSpo2() + "%"
                : "--";
        String bpText = latestVital != null && latestVital.getBloodPressure() != null && !latestVital.getBloodPressure().trim().isEmpty()
                ? latestVital.getBloodPressure() + " mmHg"
                : "--";

        String alertTypeLower = alertType.toLowerCase(Locale.US);
        String alertValueWithUnit = (alertValue + " " + alertUnit).trim();
        if (alertTypeLower.contains("heart")) {
            hrText = alertValueWithUnit;
        } else if (alertTypeLower.contains("spo2")) {
            spo2Text = alertValueWithUnit;
        } else if (alertTypeLower.contains("blood pressure")) {
            bpText = alertValueWithUnit;
        }

        int severityColor = ContextCompat.getColor(this, getSeverityColor(alertSeverity));
        if (tvAlertHRValue != null) {
            tvAlertHRValue.setText(hrText);
            tvAlertHRValue.setTextColor(severityColor);
        }
        if (tvAlertSpO2Value != null) {
            tvAlertSpO2Value.setText(spo2Text);
            tvAlertSpO2Value.setTextColor(severityColor);
        }
        if (tvAlertBPValue != null) {
            tvAlertBPValue.setText(bpText);
            tvAlertBPValue.setTextColor(severityColor);
        }
        if (tvAlertConfidenceValue != null) {
            tvAlertConfidenceValue.setText(predictionConfidence > 0 ? predictionConfidence + "%" : "--");
        }
    }

    private void setupActions() {
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        Button btnViewPatientProfile = findViewById(R.id.btnViewPatientProfile);
        if (btnViewPatientProfile == null) {
            btnViewPatientProfile = findButtonByAnyIdName("btnOpenPatientDetails");
        }
        if (btnViewPatientProfile != null) {
            btnViewPatientProfile.setOnClickListener(v -> openPatientProfile());
        } else {
            Toast.makeText(this, "Missing view id: btnViewPatientProfile", Toast.LENGTH_LONG).show();
        }

        Button resolvedAcknowledgeButton = findViewById(R.id.btnAcknowledgeAlert);
        if (resolvedAcknowledgeButton == null) {
            resolvedAcknowledgeButton = findButtonByAnyIdName("btnAcknowledge");
        }
        if (resolvedAcknowledgeButton != null) {
            final Button acknowledgeButton = resolvedAcknowledgeButton;
            updateAcknowledgeButtonState(acknowledgeButton);
            acknowledgeButton.setOnClickListener(v -> acknowledgeCurrentAlert(acknowledgeButton));
        }

        Button btnActivateResponse = findViewById(R.id.btnActivateResponse);
        if (btnActivateResponse != null) {
            btnActivateResponse.setOnClickListener(v ->
                    Toast.makeText(this, "Rapid response workflow will be wired next", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void openPatientProfile() {
        int id = parseId(patientId);
        if (id <= 0) {
            Toast.makeText(this, "Patient id unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, PatientDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PATIENT_ID, patientId);
        startActivity(intent);
    }

    private void acknowledgeCurrentAlert(Button acknowledgeButton) {
        if (alertAcknowledged) {
            Toast.makeText(this, "Alert already acknowledged", Toast.LENGTH_SHORT).show();
            updateAcknowledgeButtonState(acknowledgeButton);
            return;
        }

        int id = parseId(alertId);
        if (id <= 0) {
            Toast.makeText(this, "Invalid alert id", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = alertRepository.acknowledgeAlert(id);
        if (!success) {
            Toast.makeText(this, "Unable to acknowledge alert", Toast.LENGTH_SHORT).show();
            return;
        }

        alertAcknowledged = true;
        Toast.makeText(this, "Alert #" + alertId + " acknowledged", Toast.LENGTH_SHORT).show();
        updateAcknowledgeButtonState(acknowledgeButton);
        loadAlertFromDatabase(id);
        bindAlertHeader();
    }

    private void updateAcknowledgeButtonState(Button button) {
        if (button == null) {
            return;
        }
        button.setEnabled(!alertAcknowledged);
        if (alertAcknowledged) {
            button.setText("Acknowledged");
            button.setAlpha(0.7f);
        } else {
            button.setText("Acknowledge Alert");
            button.setAlpha(1.0f);
        }
    }

    private Button findButtonByAnyIdName(String idName) {
        int viewId = getResources().getIdentifier(idName, "id", getPackageName());
        if (viewId == 0) {
            return null;
        }
        android.view.View view = findViewById(viewId);
        return view instanceof Button ? (Button) view : null;
    }

    private void setTextIfPresent(String idName, String value) {
        int viewId = getResources().getIdentifier(idName, "id", getPackageName());
        if (viewId == 0) {
            return;
        }
        android.view.View view = findViewById(viewId);
        if (view instanceof TextView) {
            ((TextView) view).setText(safeOrDefault(value, "--"));
        }
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

    private String safeOrDefault(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value;
    }

    private String safeOrDash(String value) {
        return safeOrDefault(value, "--");
    }

    private String safeOrEmpty(String value) {
        return safeOrDefault(value, "");
    }
}
