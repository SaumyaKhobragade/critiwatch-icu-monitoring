package com.example.critiwatch;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
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
            btnRegisterPatient.setOnClickListener(v ->
                    Toast.makeText(this, "Register action pending backend/DB wiring", Toast.LENGTH_SHORT).show()
            );
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
}
