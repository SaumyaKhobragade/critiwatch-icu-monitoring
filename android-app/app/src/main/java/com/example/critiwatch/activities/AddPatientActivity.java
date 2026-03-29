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

import com.example.critiwatch.utils.SystemUiUtils;

import java.util.ArrayList;
import java.util.List;

public class AddPatientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_patient);
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
        boolean vitalsValid = validateRequiredField(R.id.etHeartRate, "Heart rate")
                && validateRequiredField(R.id.etSpO2, "SpO2")
                && validateRequiredField(R.id.etSystolicBP, "Systolic BP")
                && validateRequiredField(R.id.etDiastolicBP, "Diastolic BP")
                && validateRequiredField(R.id.etRespiratoryRate, "Respiratory rate")
                && validateRequiredField(R.id.etTemperature, "Temperature")
                && validateRequiredField(R.id.etHeight, "Height")
                && validateRequiredField(R.id.etWeight, "Weight");

        if (!vitalsValid) {
            Toast.makeText(this, "Please fill all vital fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Patient registered (frontend test)", Toast.LENGTH_SHORT).show();
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
}
