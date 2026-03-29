package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.critiwatch.adapters.PatientAdapter;
import com.example.critiwatch.database.DatabaseSeeder;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.repository.PatientRepository;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.SystemUiUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private final List<Patient> patients = new ArrayList<>();
    private PatientRepository patientRepository;
    private PatientAdapter patientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        patientRepository = new PatientRepository(this);
        DatabaseSeeder.seedIfEmpty(this);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupWardFilterSpinner();
        setupPatientRecyclerView();
        loadPatientsFromDatabase();

        Button btnAddPatient = findViewById(R.id.btnAddPatient);
        if (btnAddPatient != null) {
            btnAddPatient.setOnClickListener(v -> {
                startActivity(new Intent(this, AddPatientActivity.class));
            });
        }

        ImageView ivProfile = findViewById(R.id.ivProfile);
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
            });
        }

        ImageView ivSettings = findViewById(R.id.ivSettings);
        if (ivSettings != null) {
            ivSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
            });
        }

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPatientsFromDatabase();
    }

    private void setupPatientRecyclerView() {
        RecyclerView rvPatients = findViewById(R.id.rvPatients);
        if (rvPatients == null) {
            return;
        }

        rvPatients.setLayoutManager(new LinearLayoutManager(this));
        patientAdapter = new PatientAdapter(patients, this::openPatientDetail);
        rvPatients.setAdapter(patientAdapter);
    }

    private void loadPatientsFromDatabase() {
        patients.clear();
        patients.addAll(patientRepository.getAllPatientsWithLatestData());

        if (patientAdapter != null) {
            patientAdapter.notifyDataSetChanged();
        }
        bindSummaryStats();
    }

    private void setupWardFilterSpinner() {
        Spinner spinnerWardFilter = findViewById(R.id.spinnerWardFilter);
        if (spinnerWardFilter == null) {
            return;
        }

        List<String> wardFilters = new ArrayList<>();
        wardFilters.add("All ICU Wards");
        wardFilters.add("ICU-01 to ICU-04");
        wardFilters.add("ICU-05 to ICU-08");
        wardFilters.add("ICU-09 to ICU-12");
        wardFilters.add("Critical Priority");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                wardFilters
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWardFilter.setAdapter(adapter);
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
                return true;
            } else if (itemId == R.id.nav_alerts) {
                startActivity(new Intent(this, AlertsActivity.class));
                return true;
            } else if (itemId == R.id.nav_history) {
                startActivity(new Intent(this, GraphHistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void bindSummaryStats() {
        TextView tvTotal = findViewById(R.id.tvTotalPatients);
        TextView tvStable = findViewById(R.id.tvStablePatients);
        TextView tvWarning = findViewById(R.id.tvWarningPatients);
        TextView tvCritical = findViewById(R.id.tvCriticalPatients);

        int stableCount = 0;
        int warningCount = 0;
        int criticalCount = 0;
        for (Patient patient : patients) {
            if (Constants.RISK_CRITICAL.equalsIgnoreCase(patient.getRiskLevel())) {
                criticalCount++;
            } else if (Constants.RISK_WARNING.equalsIgnoreCase(patient.getRiskLevel())) {
                warningCount++;
            } else {
                stableCount++;
            }
        }

        if (tvTotal != null) {
            tvTotal.setText(String.valueOf(patients.size()));
        }
        if (tvStable != null) {
            tvStable.setText(String.valueOf(stableCount));
        }
        if (tvWarning != null) {
            tvWarning.setText(String.valueOf(warningCount));
        }
        if (tvCritical != null) {
            tvCritical.setText(String.valueOf(criticalCount));
        }
    }

    private void openPatientDetail(Patient patient) {
        Intent intent = new Intent(this, PatientDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PATIENT_ID, patient.getId());
        intent.putExtra(Constants.EXTRA_PATIENT_NAME, patient.getName());
        intent.putExtra(Constants.EXTRA_PATIENT_AGE, patient.getAge());
        intent.putExtra(Constants.EXTRA_PATIENT_SEX, patient.getSex());
        intent.putExtra(Constants.EXTRA_PATIENT_BED, patient.getBedNumber());
        intent.putExtra(Constants.EXTRA_PATIENT_RISK, patient.getRiskLevel());
        intent.putExtra(Constants.EXTRA_PATIENT_HEART_RATE, patient.getHeartRate());
        intent.putExtra(Constants.EXTRA_PATIENT_SPO2, patient.getSpo2());
        intent.putExtra(Constants.EXTRA_PATIENT_BP, patient.getBloodPressure());
        intent.putExtra(Constants.EXTRA_PATIENT_RR, patient.getRespiratoryRate());
        intent.putExtra(Constants.EXTRA_PATIENT_TEMP, patient.getTemperature());
        startActivity(intent);
    }
}
