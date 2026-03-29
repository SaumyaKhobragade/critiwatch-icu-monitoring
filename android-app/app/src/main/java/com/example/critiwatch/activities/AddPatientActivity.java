package com.example.critiwatch;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.Prediction;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.repository.AlertRepository;
import com.example.critiwatch.repository.PatientRepository;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.DateTimeUtils;
import com.example.critiwatch.utils.RiskUtils;
import com.example.critiwatch.utils.SystemUiUtils;

import java.util.ArrayList;
import java.util.List;

public class AddPatientActivity extends AppCompatActivity {

    private PatientRepository patientRepository;
    private AlertRepository alertRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_patient);
        patientRepository = new PatientRepository(this);
        alertRepository = new AlertRepository(this);
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

        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        } else {
            Toast.makeText(this, "Missing view id: btnCancel", Toast.LENGTH_LONG).show();
        }

        Button btnRegisterPatient = findViewById(R.id.btnRegisterPatient);
        if (btnRegisterPatient != null) {
            btnRegisterPatient.setOnClickListener(v -> attemptRegisterPatient());
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
        int age = parseIntValue(R.id.etAge);
        String sex = getSpinnerValue(R.id.spinnerSex);
        String ward = getSpinnerValue(R.id.spinnerWard);
        String bedNumber = getTextValue(R.id.etBedNumber);

        int heartRate = parseIntValue(R.id.etHeartRate);
        int spo2 = parseIntValue(R.id.etSpO2);
        int systolicBp = parseIntValue(R.id.etSystolicBP);
        int diastolicBp = parseIntValue(R.id.etDiastolicBP);
        int respiratoryRate = parseIntValue(R.id.etRespiratoryRate);
        double temperature = parseDoubleValue(R.id.etTemperature);
        double heightCm = parseDoubleValue(R.id.etHeight);
        double weightKg = parseDoubleValue(R.id.etWeight);

        String riskLevel = RiskUtils.deriveRiskLevel(heartRate, spo2, systolicBp, respiratoryRate, temperature);
        double riskScore = RiskUtils.deriveRiskScore(heartRate, spo2, systolicBp, respiratoryRate, temperature);

        Patient patient = new Patient(
                null,
                name,
                age,
                sex,
                ward,
                bedNumber,
                riskLevel,
                DateTimeUtils.now()
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
                DateTimeUtils.now()
        );

        Prediction prediction = new Prediction(
                null,
                null,
                riskLevel,
                riskScore,
                "Initial deterioration estimate based on admission vitals.",
                DateTimeUtils.now()
        );

        long patientId = patientRepository.addPatient(patient, initialVital, prediction);
        if (patientId <= 0) {
            Toast.makeText(this, "Failed to save patient locally", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Constants.RISK_STABLE.equalsIgnoreCase(riskLevel)) {
            AlertItem alertItem = new AlertItem(
                    null,
                    String.valueOf(patientId),
                    "Prediction Alert",
                    riskLevel,
                    "Patient registered with elevated deterioration indicators.",
                    DateTimeUtils.now(),
                    String.valueOf(heartRate),
                    "BPM",
                    (int) Math.round(riskScore),
                    false
            );
            alertRepository.addAlert(alertItem);
        }

        Toast.makeText(this, "Patient saved to local database", Toast.LENGTH_SHORT).show();
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

    private int parseIntValue(int editTextId) {
        String value = getTextValue(editTextId);
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return 0;
        }
    }

    private double parseDoubleValue(int editTextId) {
        String value = getTextValue(editTextId);
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            return 0d;
        }
    }
}
