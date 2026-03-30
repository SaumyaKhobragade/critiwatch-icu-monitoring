package com.example.critiwatch;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.critiwatch.database.DatabaseSeeder;
import com.example.critiwatch.database.VitalDao;
import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.Prediction;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.repository.AlertRepository;
import com.example.critiwatch.repository.PatientRepository;
import com.example.critiwatch.repository.PredictionRepository;
import com.example.critiwatch.services.NotificationHelper;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.DateTimeUtils;
import com.example.critiwatch.utils.RiskUtils;
import com.example.critiwatch.utils.SystemUiUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class PatientDetailActivity extends AppCompatActivity {

    private static final int REQ_POST_NOTIFICATIONS = 2101;

    private AlertRepository alertRepository;
    private PatientRepository patientRepository;
    private PredictionRepository predictionRepository;
    private VitalDao vitalDao;

    private String patientId;
    private String patientName;
    private int patientAge;
    private String patientSex;
    private String patientBed;
    private String patientRisk;
    private int heartRate;
    private int spo2;
    private String bloodPressure;
    private int respiratoryRate;
    private double temperature;
    private String patientCreatedAt;
    private VitalSign latestVital;
    private Prediction latestPrediction;
    private AlertItem latestAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_detail);
        DatabaseSeeder.seedIfEmpty(this);
        alertRepository = new AlertRepository(this);
        patientRepository = new PatientRepository(this);
        predictionRepository = new PredictionRepository(this);
        vitalDao = new VitalDao(this);
        NotificationHelper.ensureNotificationChannel(this);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!readPatientIdFromIntent()) {
            return;
        }
        if (!loadPatientDataFromDatabase()) {
            return;
        }
        bindPatientHeader();
        bindVitalCards();
        setupClickActions();
        setupBottomNavigation();
    }

    private boolean readPatientIdFromIntent() {
        Intent source = getIntent();
        patientId = source == null ? null : source.getStringExtra(Constants.EXTRA_PATIENT_ID);
        if (patientId == null || patientId.trim().isEmpty()) {
            Toast.makeText(this, "Missing patient id", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    private boolean loadPatientDataFromDatabase() {
        int id = parseId(patientId);
        if (id <= 0) {
            Toast.makeText(this, "Invalid patient id", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        Patient patient = patientRepository.getPatientByIdWithLatestData(id);
        if (patient == null) {
            Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        patientId = patient.getId();
        patientName = patient.getName();
        patientAge = patient.getAge();
        patientSex = patient.getSex();
        patientBed = patient.getBedNumber();
        patientRisk = patient.getRiskLevel();
        patientCreatedAt = patient.getCreatedAt();
        heartRate = patient.getHeartRate();
        spo2 = patient.getSpo2();
        bloodPressure = patient.getBloodPressure();
        respiratoryRate = patient.getRespiratoryRate();
        temperature = patient.getTemperature();

        latestVital = vitalDao.getLatestVitalByPatientId(id);
        if (latestVital != null) {
            heartRate = latestVital.getHeartRate();
            spo2 = latestVital.getSpo2();
            bloodPressure = latestVital.getBloodPressure();
            respiratoryRate = latestVital.getRespiratoryRate();
            temperature = latestVital.getTemperature();
        }

        latestPrediction = predictionRepository.getLatestPredictionByPatientId(id);
        if (latestPrediction != null && latestPrediction.getRiskLevel() != null && !latestPrediction.getRiskLevel().trim().isEmpty()) {
            patientRisk = latestPrediction.getRiskLevel();
        }

        latestAlert = alertRepository.getLatestAlertByPatientId(id);
        if (latestAlert != null && (patientRisk == null || patientRisk.trim().isEmpty())) {
            patientRisk = latestAlert.getSeverity();
        }

        if (patientName == null || patientName.trim().isEmpty()) {
            patientName = "Unknown Patient";
        }
        if (patientSex == null || patientSex.trim().isEmpty()) {
            patientSex = "Unknown";
        }
        if (patientBed == null || patientBed.trim().isEmpty()) {
            patientBed = "-";
        }
        if (patientRisk == null || patientRisk.trim().isEmpty()) {
            patientRisk = Constants.RISK_WARNING;
        }
        if (bloodPressure == null || bloodPressure.trim().isEmpty()) {
            bloodPressure = "--/--";
        }

        return true;
    }

    private void bindPatientHeader() {
        TextView tvPatientNameLarge = findViewById(R.id.tvPatientNameLarge);
        TextView tvPatientMetaLarge = findViewById(R.id.tvPatientMetaLarge);
        TextView tvStatusBadge = findViewById(R.id.tvStatusBadge);
        TextView tvPatientDetailTitle = findViewById(R.id.tvPatientDetailTitle);
        TextView tvPatientIdChip = findViewById(R.id.tvPatientIdChip);
        TextView tvAdmittedChip = findViewById(R.id.tvAdmittedChip);
        TextView tvAlertBannerMessage = findViewById(R.id.tvAlertBannerMessage);
        TextView tvPredictionSummary = findViewById(R.id.tvPredictionSummary);

        if (tvPatientNameLarge != null) {
            tvPatientNameLarge.setText(patientName);
        }
        if (tvPatientMetaLarge != null) {
            tvPatientMetaLarge.setText(patientAge + "y • " + patientSex + " • Bed " + patientBed);
        }
        if (tvPatientDetailTitle != null) {
            tvPatientDetailTitle.setText("Patient Detail • " + patientId);
        }
        if (tvPatientIdChip != null) {
            tvPatientIdChip.setText("ID: " + patientId);
        }
        if (tvAdmittedChip != null) {
            if (latestPrediction != null && latestPrediction.getCreatedAt() != null) {
                tvAdmittedChip.setText("Updated: " + DateTimeUtils.toRelativeTime(latestPrediction.getCreatedAt()));
            } else if (patientCreatedAt != null && !patientCreatedAt.trim().isEmpty()) {
                tvAdmittedChip.setText("Admitted: " + DateTimeUtils.toRelativeTime(patientCreatedAt));
            } else {
                tvAdmittedChip.setText(getMockAdmissionText(patientRisk));
            }
        }

        if (tvStatusBadge != null) {
            tvStatusBadge.setText(patientRisk.toUpperCase(Locale.US));
            if (Constants.RISK_CRITICAL.equalsIgnoreCase(patientRisk)) {
                tvStatusBadge.setBackgroundResource(R.drawable.bg_chip_critical);
                tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_critical));
            } else if (Constants.RISK_WARNING.equalsIgnoreCase(patientRisk)) {
                tvStatusBadge.setBackgroundResource(R.drawable.bg_chip_warning);
                tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
            } else {
                tvStatusBadge.setBackgroundResource(R.drawable.bg_chip_stable);
                tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_stable));
            }
        }

        if (tvAlertBannerMessage != null) {
            if (latestPrediction != null && latestPrediction.getSummary() != null && !latestPrediction.getSummary().trim().isEmpty()) {
                tvAlertBannerMessage.setText(latestPrediction.getSummary());
                tvAlertBannerMessage.setTextColor(ContextCompat.getColor(this, getSeverityColorForMetric(patientRisk)));
            } else if (latestAlert != null && latestAlert.getDescription() != null && !latestAlert.getDescription().trim().isEmpty()) {
                tvAlertBannerMessage.setText(latestAlert.getDescription());
                tvAlertBannerMessage.setTextColor(ContextCompat.getColor(this, getSeverityColorForMetric(latestAlert.getSeverity())));
            } else if (Constants.RISK_CRITICAL.equalsIgnoreCase(patientRisk)) {
                tvAlertBannerMessage.setText("CRITICAL: High deterioration risk detected. Sepsis protocol review recommended.");
                tvAlertBannerMessage.setTextColor(ContextCompat.getColor(this, R.color.status_critical));
            } else if (Constants.RISK_WARNING.equalsIgnoreCase(patientRisk)) {
                tvAlertBannerMessage.setText("WARNING: Deterioration trend detected. Closely monitor respiratory and SpO2 changes.");
                tvAlertBannerMessage.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
            } else {
                tvAlertBannerMessage.setText("STABLE: Current trend is stable. Continue standard ICU monitoring protocol.");
                tvAlertBannerMessage.setTextColor(ContextCompat.getColor(this, R.color.status_stable));
            }
        }

        if (tvPredictionSummary != null) {
            if (latestPrediction != null && latestPrediction.getSummary() != null && !latestPrediction.getSummary().trim().isEmpty()) {
                tvPredictionSummary.setText(
                        String.format(Locale.US, "%.0f%% risk score • %s",
                                latestPrediction.getRiskScore(),
                                latestPrediction.getSummary())
                );
            } else {
                tvPredictionSummary.setText("Run prediction to generate risk insight from latest vitals.");
            }
        }
    }

    private void bindVitalCards() {
        bindMetricCard(
                findViewById(R.id.cardVitalHeartRate),
                "Heart Rate",
                String.valueOf(heartRate),
                "bpm",
                getSeverityColorForMetric(patientRisk)
        );
        bindMetricCard(
                findViewById(R.id.cardVitalSpo2),
                "SpO2",
                spo2 + "%",
                "%",
                getSpo2Color(spo2)
        );
        bindMetricCard(
                findViewById(R.id.cardVitalBloodPressure),
                "Blood Pressure",
                bloodPressure,
                "mmHg",
                getSeverityColorForMetric(patientRisk)
        );
        bindMetricCard(
                findViewById(R.id.cardVitalRespRate),
                "Resp Rate",
                String.valueOf(respiratoryRate),
                "/min",
                getSeverityColorForMetric(patientRisk)
        );
        bindMetricCard(
                findViewById(R.id.cardVitalTemperature),
                "Temperature",
                String.format(Locale.US, "%.1f", temperature),
                "C",
                getTemperatureColor(temperature)
        );
        bindMetricCard(
                findViewById(R.id.cardVitalGlucose),
                "Glucose",
                "118",
                "mg/dL",
                R.color.status_stable
        );
    }

    private void bindMetricCard(View cardView, String label, String value, String unit, int colorRes) {
        if (cardView == null) {
            return;
        }

        TextView tvMetricLabel = cardView.findViewById(R.id.tvMetricLabel);
        TextView tvMetricValue = cardView.findViewById(R.id.tvMetricValue);
        TextView tvMetricUnit = cardView.findViewById(R.id.tvMetricUnit);
        View vStatusIndicator = cardView.findViewById(R.id.vStatusIndicator);

        if (tvMetricLabel != null) {
            tvMetricLabel.setText(label.toUpperCase(Locale.US));
        }
        if (tvMetricValue != null) {
            tvMetricValue.setText(value);
            tvMetricValue.setTextColor(ContextCompat.getColor(this, colorRes));
        }
        if (tvMetricUnit != null) {
            tvMetricUnit.setText(unit);
        }
        if (vStatusIndicator != null) {
            vStatusIndicator.setBackgroundColor(ContextCompat.getColor(this, colorRes));
        }
    }

    private void setupClickActions() {
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        ImageView ivMore = findViewById(R.id.ivMore);
        if (ivMore != null) {
            ivMore.setOnClickListener(this::showPatientActionsMenu);
        }

        Button btnViewHistory = findViewById(R.id.btnViewHistory);
        if (btnViewHistory != null) {
            btnViewHistory.setOnClickListener(v -> {
                Intent intent = new Intent(this, GraphHistoryActivity.class);
                intent.putExtra(Constants.EXTRA_PATIENT_ID, patientId);
                intent.putExtra(Constants.EXTRA_PATIENT_NAME, patientName);
                intent.putExtra(Constants.EXTRA_PATIENT_BED, patientBed);
                startActivity(intent);
            });
        }

        Button btnRunPrediction = findViewById(R.id.btnRunPrediction);
        if (btnRunPrediction != null) {
            btnRunPrediction.setOnClickListener(v -> runPredictionForCurrentPatient());
        }

        Button btnActivateResponse = findViewById(R.id.btnActivateResponse);
        if (btnActivateResponse != null) {
            btnActivateResponse.setOnClickListener(v -> openAlertDetail());
        }

        Button btnAcknowledgeAlert = findViewById(R.id.btnAcknowledgeAlert);
        if (btnAcknowledgeAlert != null) {
            btnAcknowledgeAlert.setOnClickListener(v -> acknowledgeLatestAlert());
        }

        Button btnAddNote = findViewById(R.id.btnAddNote);
        if (btnAddNote != null) {
            btnAddNote.setOnClickListener(v ->
                    Toast.makeText(this, "Clinical note input will be wired in next step", Toast.LENGTH_SHORT).show()
            );
        }

        View llAlertBanner = findViewById(R.id.llAlertBanner);
        if (llAlertBanner != null) {
            llAlertBanner.setOnClickListener(v -> openAlertDetail());
        }
    }

    private void runPredictionForCurrentPatient() {
        int id = parseId(patientId);
        if (id <= 0) {
            Toast.makeText(this, "Invalid patient id", Toast.LENGTH_SHORT).show();
            return;
        }

        latestVital = vitalDao.getLatestVitalByPatientId(id);
        if (latestVital == null) {
            Toast.makeText(this, "No vitals found. Add vitals before running prediction.", Toast.LENGTH_SHORT).show();
            return;
        }

        RiskUtils.EvaluationResult evaluation = RiskUtils.evaluate(latestVital);
        patientRisk = evaluation.getRiskLevel();

        Prediction prediction = new Prediction(
                null,
                patientId,
                evaluation.getRiskLevel(),
                evaluation.getRiskScore(),
                evaluation.getSummary(),
                DateTimeUtils.now()
        );
        long predictionId = predictionRepository.addPrediction(prediction);
        if (predictionId <= 0) {
            Toast.makeText(this, "Prediction save failed", Toast.LENGTH_SHORT).show();
            return;
        }
        prediction.setId(String.valueOf(predictionId));
        latestPrediction = prediction;
        patientRepository.updatePatientRiskLevel(id, patientRisk);

        heartRate = latestVital.getHeartRate();
        spo2 = latestVital.getSpo2();
        bloodPressure = latestVital.getBloodPressure();
        respiratoryRate = latestVital.getRespiratoryRate();
        temperature = latestVital.getTemperature();

        boolean alertCreated = false;
        boolean notificationSent = false;
        if (!Constants.RISK_STABLE.equalsIgnoreCase(evaluation.getRiskLevel())) {
            AlertItem alertItem = new AlertItem(
                    null,
                    patientId,
                    evaluation.getAlertType(),
                    evaluation.getRiskLevel(),
                    evaluation.getSummary(),
                    DateTimeUtils.now(),
                    buildPrimaryAlertValue(evaluation, latestVital),
                    buildPrimaryAlertUnit(evaluation),
                    (int) Math.round(evaluation.getRiskScore()),
                    false
            );
            alertItem.setPatientName(patientName);
            alertItem.setPatientAge(patientAge);
            alertItem.setPatientSex(patientSex);
            alertItem.setPatientBed(patientBed);
            alertItem.setPatientRisk(patientRisk);

            long alertId = alertRepository.addAlert(alertItem);
            if (alertId > 0) {
                alertCreated = true;
                latestAlert = alertRepository.getAlertById((int) alertId);
                if (latestAlert == null) {
                    alertItem.setId(String.valueOf(alertId));
                    latestAlert = alertItem;
                }
                notificationSent = NotificationHelper.showAlertNotification(this, latestAlert);
                if (!notificationSent && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationPermissionIfNeeded();
                }
            }
        }

        bindPatientHeader();
        bindVitalCards();

        String toastMessage = "Prediction updated: " + patientRisk;
        if (alertCreated) {
            toastMessage += notificationSent ? " (alert + notification sent)" : " (alert created)";
        }
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    private String buildPrimaryAlertValue(RiskUtils.EvaluationResult evaluation, VitalSign vitalSign) {
        String alertType = evaluation.getAlertType().toLowerCase(Locale.US);
        if (alertType.contains("spo2")) {
            return String.valueOf(vitalSign.getSpo2());
        }
        if (alertType.contains("blood pressure")) {
            return vitalSign.getBloodPressure();
        }
        if (alertType.contains("heart")) {
            return String.valueOf(vitalSign.getHeartRate());
        }
        return String.format(Locale.US, "%.0f", evaluation.getRiskScore());
    }

    private String buildPrimaryAlertUnit(RiskUtils.EvaluationResult evaluation) {
        String alertType = evaluation.getAlertType().toLowerCase(Locale.US);
        if (alertType.contains("spo2")) {
            return "%";
        }
        if (alertType.contains("blood pressure")) {
            return "mmHg";
        }
        if (alertType.contains("heart")) {
            return "BPM";
        }
        return "score";
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                REQ_POST_NOTIFICATIONS
        );
    }

    private void acknowledgeLatestAlert() {
        if (latestAlert == null) {
            Toast.makeText(this, "No active alert available", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = parseId(latestAlert.getId());
        if (id <= 0) {
            Toast.makeText(this, "Invalid alert id", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = alertRepository.acknowledgeAlert(id);
        Toast.makeText(this, success ? "Alert acknowledged" : "Unable to acknowledge alert", Toast.LENGTH_SHORT).show();
        if (success) {
            latestAlert = alertRepository.getLatestAlertByPatientId(parseId(patientId));
        }
    }

    private void showPatientActionsMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.inflate(R.menu.menu_patient_detail_actions);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuDeletePatient) {
                confirmDeletePatient();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void confirmDeletePatient() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Patient")
                .setMessage("This will remove the patient and related local vitals, predictions, and alerts from this device.")
                .setPositiveButton("Delete", (dialog, which) -> deletePatient())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePatient() {
        int patientDbId = parseId(patientId);
        if (patientDbId <= 0) {
            Toast.makeText(this, "Unable to delete: invalid patient id", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean deleted = patientRepository.deletePatient(patientDbId);
        if (!deleted) {
            Toast.makeText(this, "Patient deletion failed", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Patient deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void openAlertDetail() {
        if (latestAlert == null) {
            Toast.makeText(this, "No alert available for this patient", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, NotificationDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ALERT_ID, latestAlert.getId());
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.nav_alerts) {
                startActivity(new Intent(this, AlertsActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_POST_NOTIFICATIONS) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(
                    this,
                    granted ? "Notification permission granted" : "Notification permission denied",
                    Toast.LENGTH_SHORT
            ).show();
        }
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

    private int getSeverityColorForMetric(String risk) {
        if (Constants.RISK_CRITICAL.equalsIgnoreCase(risk)) {
            return R.color.status_critical;
        }
        if (Constants.RISK_WARNING.equalsIgnoreCase(risk)) {
            return R.color.status_warning;
        }
        return R.color.status_stable;
    }

    private int getSpo2Color(int spo2Value) {
        if (spo2Value < 90) {
            return R.color.status_critical;
        }
        if (spo2Value < 94) {
            return R.color.status_warning;
        }
        return R.color.status_stable;
    }

    private int getTemperatureColor(double temp) {
        if (temp >= 38.5d) {
            return R.color.status_critical;
        }
        if (temp >= 37.5d) {
            return R.color.status_warning;
        }
        return R.color.status_stable;
    }

    private String getMockAdmissionText(String risk) {
        if (Constants.RISK_CRITICAL.equalsIgnoreCase(risk)) {
            return "Admitted: 2 days ago";
        }
        if (Constants.RISK_WARNING.equalsIgnoreCase(risk)) {
            return "Admitted: 1 day ago";
        }
        return "Admitted: 8 hours ago";
    }
}
