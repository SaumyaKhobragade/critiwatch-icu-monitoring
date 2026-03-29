package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private static final String FILTER_ALL_WARDS = "All ICU Wards";
    private static final String FILTER_ICU_1_4 = "ICU-01 to ICU-04";
    private static final String FILTER_ICU_5_8 = "ICU-05 to ICU-08";
    private static final String FILTER_ICU_9_12 = "ICU-09 to ICU-12";
    private static final String FILTER_CRITICAL = "Critical Priority";

    private final List<Patient> allPatients = new ArrayList<>();
    private final List<Patient> filteredPatients = new ArrayList<>();
    private PatientRepository patientRepository;
    private PatientAdapter patientAdapter;
    private String selectedWardFilter = FILTER_ALL_WARDS;
    private String searchQuery = "";

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
        setupSearchBar();
        setupPatientRecyclerView();
        loadPatientsFromDatabase();

        Button btnAddPatient = findViewById(R.id.btnAddPatient);
        if (btnAddPatient != null) {
            btnAddPatient.setOnClickListener(v -> startActivity(new Intent(this, AddPatientActivity.class)));
        }

        ImageView ivProfile = findViewById(R.id.ivProfile);
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        ImageView ivSettings = findViewById(R.id.ivSettings);
        if (ivSettings != null) {
            ivSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
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
        patientAdapter = new PatientAdapter(filteredPatients, this::openPatientDetail);
        rvPatients.setAdapter(patientAdapter);
    }

    private void loadPatientsFromDatabase() {
        allPatients.clear();
        allPatients.addAll(patientRepository.getAllPatientsWithLatestData());
        applyPatientFilters();
    }

    private void setupSearchBar() {
        EditText etSearch = findViewById(R.id.etSearch);
        if (etSearch == null) {
            return;
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s == null ? "" : s.toString().trim();
                applyPatientFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No-op
            }
        });
    }

    private void setupWardFilterSpinner() {
        Spinner spinnerWardFilter = findViewById(R.id.spinnerWardFilter);
        if (spinnerWardFilter == null) {
            return;
        }

        List<String> wardFilters = new ArrayList<>();
        wardFilters.add(FILTER_ALL_WARDS);
        wardFilters.add(FILTER_ICU_1_4);
        wardFilters.add(FILTER_ICU_5_8);
        wardFilters.add(FILTER_ICU_9_12);
        wardFilters.add(FILTER_CRITICAL);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                wardFilters
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWardFilter.setAdapter(adapter);

        spinnerWardFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedWardFilter = wardFilters.get(position);
                applyPatientFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedWardFilter = FILTER_ALL_WARDS;
                applyPatientFilters();
            }
        });
    }

    private void applyPatientFilters() {
        filteredPatients.clear();
        for (Patient patient : allPatients) {
            if (matchesWardFilter(patient) && matchesSearchQuery(patient)) {
                filteredPatients.add(patient);
            }
        }

        if (patientAdapter != null) {
            patientAdapter.notifyDataSetChanged();
        }
        bindSummaryStats();
    }

    private boolean matchesSearchQuery(Patient patient) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return true;
        }

        String query = searchQuery.toLowerCase(Locale.US);
        String patientName = safeLower(patient.getName());
        String patientId = safeLower(patient.getId());
        String bedNumber = safeLower(patient.getBedNumber());
        String risk = safeLower(patient.getRiskLevel());

        return patientName.contains(query)
                || patientId.contains(query)
                || bedNumber.contains(query)
                || risk.contains(query);
    }

    private boolean matchesWardFilter(Patient patient) {
        if (FILTER_ALL_WARDS.equals(selectedWardFilter)) {
            return true;
        }
        if (FILTER_CRITICAL.equals(selectedWardFilter)) {
            return Constants.RISK_CRITICAL.equalsIgnoreCase(patient.getRiskLevel());
        }

        int wardNumber = extractWardNumber(patient.getWard(), patient.getBedNumber());
        if (wardNumber <= 0) {
            return false;
        }

        if (FILTER_ICU_1_4.equals(selectedWardFilter)) {
            return wardNumber >= 1 && wardNumber <= 4;
        }
        if (FILTER_ICU_5_8.equals(selectedWardFilter)) {
            return wardNumber >= 5 && wardNumber <= 8;
        }
        if (FILTER_ICU_9_12.equals(selectedWardFilter)) {
            return wardNumber >= 9 && wardNumber <= 12;
        }

        return true;
    }

    private int extractWardNumber(String ward, String bedNumber) {
        String source = ward;
        if (source == null || source.trim().isEmpty()) {
            source = bedNumber;
        }
        if (source == null) {
            return -1;
        }

        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (Character.isDigit(c)) {
                digits.append(c);
                if (digits.length() == 2) {
                    break;
                }
            }
        }

        if (digits.length() == 0) {
            return -1;
        }

        try {
            return Integer.parseInt(digits.toString());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.US);
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
        for (Patient patient : filteredPatients) {
            if (Constants.RISK_CRITICAL.equalsIgnoreCase(patient.getRiskLevel())) {
                criticalCount++;
            } else if (Constants.RISK_WARNING.equalsIgnoreCase(patient.getRiskLevel())) {
                warningCount++;
            } else {
                stableCount++;
            }
        }

        if (tvTotal != null) {
            tvTotal.setText(String.valueOf(filteredPatients.size()));
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
        startActivity(intent);
    }
}
