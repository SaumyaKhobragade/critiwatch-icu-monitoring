package com.example.critiwatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.Prediction;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.repository.AlertRepository;
import com.example.critiwatch.repository.PatientRepository;
import com.example.critiwatch.services.NotificationHelper;
import com.example.critiwatch.services.ValidationService;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.DateTimeUtils;
import com.example.critiwatch.utils.RiskUtils;
import com.example.critiwatch.utils.SystemUiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPatientActivity extends AppCompatActivity {

    private static final int REQ_POST_NOTIFICATIONS = 2102;
    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 130;
    private static final int MIN_HEART_RATE = 20;
    private static final int MAX_HEART_RATE = 250;
    private static final int MIN_SPO2 = 50;
    private static final int MAX_SPO2 = 100;
    private static final int MIN_SYSTOLIC_BP = 40;
    private static final int MAX_SYSTOLIC_BP = 260;
    private static final int MIN_DIASTOLIC_BP = 20;
    private static final int MAX_DIASTOLIC_BP = 180;
    private static final int MIN_RESPIRATORY_RATE = 5;
    private static final int MAX_RESPIRATORY_RATE = 80;
    private static final double MIN_TEMPERATURE = 25.0d;
    private static final double MAX_TEMPERATURE = 113.0d;
    private static final double MIN_HEIGHT_CM = 30.0d;
    private static final double MAX_HEIGHT_CM = 250.0d;
    private static final double MIN_WEIGHT_KG = 1.0d;
    private static final double MAX_WEIGHT_KG = 400.0d;

    private PatientRepository patientRepository;
    private AlertRepository alertRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_patient);
        patientRepository = new PatientRepository(this);
        alertRepository = new AlertRepository(this);
        NotificationHelper.ensureNotificationChannel(this);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomInset = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset);
            return insets;
        });
        setupSexSpinner();
        setupWardSpinner();

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        } else {
            Toast.makeText(this, "Missing view id: ivBack", Toast.LENGTH_LONG).show();
        }

        Button btnBack = findOptionalButtonByName("btnBack");
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        } else {
            Toast.makeText(this, "Missing view id: btnCancel", Toast.LENGTH_LONG).show();
        }

        Button btnRegisterPatient = findViewById(R.id.btnRegisterPatient);
        if (btnRegisterPatient == null) {
            btnRegisterPatient = findOptionalButtonByName("btnSavePatient");
        }
        if (btnRegisterPatient != null) {
            btnRegisterPatient.setOnClickListener(v -> attemptRegisterPatient());
        }

        Button btnResetPatient = findOptionalButtonByName("btnResetPatient");
        if (btnResetPatient != null) {
            btnResetPatient.setOnClickListener(v -> resetForm());
        }

        View btnScanWristband = findViewById(R.id.btnScanWristband);
        if (btnScanWristband != null) {
            btnScanWristband.setOnClickListener(v ->
                    Toast.makeText(this, "Wristband scanner not implemented yet", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void setupSexSpinner() {
        Spinner spinnerSex = findViewById(R.id.spinnerSex);
        if (spinnerSex == null) {
            return;
        }

        List<String> sexOptions = new ArrayList<>();
        sexOptions.add("Select Sex");
        sexOptions.add("Female");
        sexOptions.add("Male");
        sexOptions.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sexOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(adapter);
    }

    private void setupWardSpinner() {
        Spinner spinnerWard = findViewById(R.id.spinnerWard);
        if (spinnerWard == null) {
            return;
        }

        List<String> wardOptions = new ArrayList<>();
        wardOptions.add("Select Ward / Unit");
        wardOptions.add("ICU-01");
        wardOptions.add("ICU-02");
        wardOptions.add("ICU-03");
        wardOptions.add("ICU-04");
        wardOptions.add("ICU-05");
        wardOptions.add("ICU-06");
        wardOptions.add("ICU-07");
        wardOptions.add("ICU-08");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                wardOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWard.setAdapter(adapter);
    }

    private void attemptRegisterPatient() {
        boolean patientInfoValid = validateRequiredField(R.id.etPatientName, "Patient name")
                && validateRequiredField(R.id.etAge, "Age")
                && validateSpinnerSelection(R.id.spinnerSex, "Sex")
                && validateSpinnerSelection(R.id.spinnerWard, "Ward / Unit")
                && validateRequiredField(R.id.etBedNumber, "Bed number");

        boolean vitalsValid = validateRequiredField(R.id.etHeartRate, "Heart rate")
                && validateRequiredField(R.id.etSpO2, "SpO2")
                && validateRequiredField(R.id.etSystolicBP, "Systolic BP")
                && validateRequiredField(R.id.etDiastolicBP, "Diastolic BP")
                && validateRequiredField(R.id.etRespiratoryRate, "Respiratory rate")
                && validateRequiredField(R.id.etTemperature, "Temperature")
                && validateRequiredField(R.id.etHeight, "Height")
                && validateRequiredField(R.id.etWeight, "Weight");

        if (!patientInfoValid || !vitalsValid) {
            Toast.makeText(this, "Please fill all required patient and vital fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = getTextValue(R.id.etPatientName);
        Integer age = parseRequiredIntField(R.id.etAge, "Age", MIN_AGE, MAX_AGE);
        String sex = getSpinnerValue(R.id.spinnerSex);
        String ward = getSpinnerValue(R.id.spinnerWard);
        String bedNumber = getTextValue(R.id.etBedNumber);

        Integer heartRate = parseRequiredIntField(R.id.etHeartRate, "Heart rate", MIN_HEART_RATE, MAX_HEART_RATE);
        Integer spo2 = parseRequiredIntField(R.id.etSpO2, "SpO2", MIN_SPO2, MAX_SPO2);
        Integer systolicBp = parseRequiredIntField(R.id.etSystolicBP, "Systolic BP", MIN_SYSTOLIC_BP, MAX_SYSTOLIC_BP);
        Integer diastolicBp = parseRequiredIntField(R.id.etDiastolicBP, "Diastolic BP", MIN_DIASTOLIC_BP, MAX_DIASTOLIC_BP);
        Integer respiratoryRate = parseRequiredIntField(R.id.etRespiratoryRate, "Respiratory rate", MIN_RESPIRATORY_RATE, MAX_RESPIRATORY_RATE);
        Double temperature = parseRequiredDoubleField(R.id.etTemperature, "Temperature", MIN_TEMPERATURE, MAX_TEMPERATURE);
        Double heightCm = parseRequiredDoubleField(R.id.etHeight, "Height", MIN_HEIGHT_CM, MAX_HEIGHT_CM);
        Double weightKg = parseRequiredDoubleField(R.id.etWeight, "Weight", MIN_WEIGHT_KG, MAX_WEIGHT_KG);

        if (age == null || heartRate == null || spo2 == null || systolicBp == null
                || diastolicBp == null || respiratoryRate == null || temperature == null
                || heightCm == null || weightKg == null) {
            Toast.makeText(this, "Please correct the highlighted values", Toast.LENGTH_SHORT).show();
            return;
        }

        String capturedAt = resolveTimestampForNewEntry();

        RiskUtils.EvaluationResult evaluationResult = RiskUtils.evaluate(
                heartRate,
                spo2,
                systolicBp,
                diastolicBp,
                respiratoryRate,
                temperature
        );
        String riskLevel = evaluationResult.getRiskLevel();
        double riskScore = evaluationResult.getRiskScore();

        Patient patient = new Patient(
                null,
                name,
                age,
                sex,
                ward,
                bedNumber,
                riskLevel,
                capturedAt
        );

        VitalSign initialVital = new VitalSign(
                null,
                null,
                heartRate,
                spo2,
                systolicBp,
                diastolicBp,
                respiratoryRate,
                temperature,
                heightCm,
                weightKg,
                capturedAt
        );

        Prediction prediction = new Prediction(
                null,
                null,
                riskLevel,
                riskScore,
                evaluationResult.getSummary(),
                capturedAt
        );

        long patientId = patientRepository.addPatient(patient, initialVital, prediction);
        if (patientId <= 0) {
            Toast.makeText(this, "Failed to save patient locally", Toast.LENGTH_LONG).show();
            return;
        }

        boolean alertCreated = false;
        boolean notificationSent = false;
        if (!Constants.RISK_STABLE.equalsIgnoreCase(riskLevel)) {
            AlertItem alertItem = new AlertItem(
                    null,
                    String.valueOf(patientId),
                    evaluationResult.getAlertType(),
                    riskLevel,
                    evaluationResult.getSummary(),
                    capturedAt,
                    String.valueOf(heartRate),
                    "BPM",
                    (int) Math.round(riskScore),
                    false
            );
            alertItem.setPatientName(name);
            alertItem.setPatientAge(age);
            alertItem.setPatientSex(sex);
            alertItem.setPatientBed(bedNumber);
            alertItem.setPatientRisk(riskLevel);

            long alertId = alertRepository.addAlert(alertItem);
            if (alertId > 0) {
                alertCreated = true;
                alertItem.setId(String.valueOf(alertId));
                notificationSent = NotificationHelper.showAlertNotification(this, alertItem);
                if (!notificationSent) {
                    requestNotificationPermissionIfNeeded();
                }
            }
        }

        if (alertCreated) {
            Toast.makeText(
                    this,
                    notificationSent
                            ? "Patient saved. Risk alert created and notification sent."
                            : "Patient saved. Risk alert created.",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(this, "Patient saved to local database", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private boolean validateRequiredField(int editTextId, String fieldLabel) {
        EditText field = findViewById(editTextId);
        if (field == null) {
            Toast.makeText(this, "Missing view id: " + getResources().getResourceEntryName(editTextId), Toast.LENGTH_LONG).show();
            return false;
        }

        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError(fieldLabel + " is required");
            field.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateSpinnerSelection(int spinnerId, String fieldLabel) {
        Spinner spinner = findViewById(spinnerId);
        if (spinner == null) {
            Toast.makeText(this, "Missing view id: " + getResources().getResourceEntryName(spinnerId), Toast.LENGTH_LONG).show();
            return false;
        }

        if (spinner.getSelectedItemPosition() <= 0) {
            Toast.makeText(this, fieldLabel + " is required", Toast.LENGTH_SHORT).show();
            spinner.requestFocus();
            return false;
        }

        return true;
    }

    private String getTextValue(int editTextId) {
        EditText editText = findViewById(editTextId);
        return editText == null ? "" : editText.getText().toString().trim();
    }

    private String getSpinnerValue(int spinnerId) {
        Spinner spinner = findViewById(spinnerId);
        if (spinner == null || spinner.getSelectedItem() == null) {
            return "";
        }
        return spinner.getSelectedItem().toString();
    }

    private Integer parseRequiredIntField(int editTextId, String fieldLabel, int min, int max) {
        EditText field = findViewById(editTextId);
        if (field == null) {
            Toast.makeText(this, "Missing view id: " + getResources().getResourceEntryName(editTextId), Toast.LENGTH_LONG).show();
            return null;
        }
        Integer value = ValidationService.tryParseInt(field.getText().toString());
        if (value == null) {
            field.setError("Enter a valid number for " + fieldLabel);
            field.requestFocus();
            return null;
        }
        if (!ValidationService.isInRange(value, min, max)) {
            field.setError(fieldLabel + " must be between " + min + " and " + max);
            field.requestFocus();
            return null;
        }
        return value;
    }

    private Double parseRequiredDoubleField(int editTextId, String fieldLabel, double min, double max) {
        EditText field = findViewById(editTextId);
        if (field == null) {
            Toast.makeText(this, "Missing view id: " + getResources().getResourceEntryName(editTextId), Toast.LENGTH_LONG).show();
            return null;
        }
        Double value = ValidationService.tryParseDouble(field.getText().toString());
        if (value == null) {
            field.setError("Enter a valid value for " + fieldLabel);
            field.requestFocus();
            return null;
        }
        if (!ValidationService.isInRange(value, min, max)) {
            field.setError(fieldLabel + " must be between " + formatNumber(min) + " and " + formatNumber(max));
            field.requestFocus();
            return null;
        }
        return value;
    }

    private String resolveTimestampForNewEntry() {
        String dateText = getOptionalTextByIdName("tvSelectedDate", "etDate", "tvDate");
        String timeText = getOptionalTextByIdName("tvSelectedTime", "etTime", "tvTime");

        if (ValidationService.isBlank(dateText) || ValidationService.isBlank(timeText)) {
            return DateTimeUtils.now();
        }

        String dateTimeText = dateText.trim() + " " + timeText.trim();
        List<String> patterns = Arrays.asList(
                "dd/MM/yyyy HH:mm",
                "dd-MM-yyyy HH:mm",
                "yyyy-MM-dd HH:mm",
                "MM/dd/yyyy HH:mm",
                "dd/MM/yyyy hh:mm a",
                "dd-MM-yyyy hh:mm a",
                "yyyy-MM-dd hh:mm a",
                "MM/dd/yyyy hh:mm a"
        );

        for (String pattern : patterns) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(pattern, Locale.US);
                parser.setLenient(false);
                Date parsed = parser.parse(dateTimeText);
                if (parsed != null) {
                    return DateTimeUtils.format(parsed);
                }
            } catch (ParseException ignored) {
                // Try next format.
            }
        }

        Toast.makeText(this, "Invalid date/time format. Using current time.", Toast.LENGTH_SHORT).show();
        return DateTimeUtils.now();
    }

    private String getOptionalTextByIdName(String... idNames) {
        for (String idName : idNames) {
            int viewId = getResources().getIdentifier(idName, "id", getPackageName());
            if (viewId == 0) {
                continue;
            }
            View view = findViewById(viewId);
            if (view instanceof TextView) {
                String value = ((TextView) view).getText().toString();
                if (!ValidationService.isBlank(value)) {
                    return value.trim();
                }
            }
        }
        return "";
    }

    private Button findOptionalButtonByName(String idName) {
        int viewId = getResources().getIdentifier(idName, "id", getPackageName());
        if (viewId == 0) {
            return null;
        }
        View view = findViewById(viewId);
        return view instanceof Button ? (Button) view : null;
    }

    private void resetForm() {
        int[] editIds = new int[]{
                R.id.etPatientName,
                R.id.etPatientId,
                R.id.etAge,
                R.id.etBedNumber,
                R.id.etDiagnosis,
                R.id.etHeartRate,
                R.id.etSpO2,
                R.id.etSystolicBP,
                R.id.etDiastolicBP,
                R.id.etRespiratoryRate,
                R.id.etTemperature,
                R.id.etHeight,
                R.id.etWeight
        };

        for (int editId : editIds) {
            EditText field = findViewById(editId);
            if (field != null) {
                field.setText("");
                field.setError(null);
            }
        }

        Spinner spinnerSex = findViewById(R.id.spinnerSex);
        if (spinnerSex != null) {
            spinnerSex.setSelection(0);
        }

        Spinner spinnerWard = findViewById(R.id.spinnerWard);
        if (spinnerWard != null) {
            spinnerWard.setSelection(0);
        }

        clearOptionalTextByIdName("tvSelectedDate", "etDate", "tvDate");
        clearOptionalTextByIdName("tvSelectedTime", "etTime", "tvTime");

        EditText etPatientName = findViewById(R.id.etPatientName);
        if (etPatientName != null) {
            etPatientName.requestFocus();
        }

        Toast.makeText(this, "Form reset", Toast.LENGTH_SHORT).show();
    }

    private void clearOptionalTextByIdName(String... idNames) {
        for (String idName : idNames) {
            int viewId = getResources().getIdentifier(idName, "id", getPackageName());
            if (viewId == 0) {
                continue;
            }
            View view = findViewById(viewId);
            if (view instanceof TextView) {
                ((TextView) view).setText("");
            }
        }
    }

    private String formatNumber(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.format(Locale.US, "%.1f", value);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                REQ_POST_NOTIFICATIONS
        );
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
}
