package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.critiwatch.adapters.VitalHistoryAdapter;
import com.example.critiwatch.database.DatabaseSeeder;
import com.example.critiwatch.database.PatientDao;
import com.example.critiwatch.database.VitalDao;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.SystemUiUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class GraphHistoryActivity extends AppCompatActivity {

    private final List<VitalSign> vitalHistory = new ArrayList<>();
    private String patientId;
    private String patientName;
    private String patientBed;
    private VitalDao vitalDao;
    private PatientDao patientDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_graph_history);
        DatabaseSeeder.seedIfEmpty(this);
        vitalDao = new VitalDao(this);
        patientDao = new PatientDao(this);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!readPatientExtras()) {
            return;
        }
        bindPatientHeader();
        setupMetricSpinner();
        loadVitalHistoryFromDatabase();
        setupReadingRecyclerView();
        setupClickActions();
        setupBottomNavigation();
    }

    private boolean readPatientExtras() {
        Intent source = getIntent();
        patientId = source.getStringExtra(Constants.EXTRA_PATIENT_ID);
        patientName = source.getStringExtra(Constants.EXTRA_PATIENT_NAME);
        patientBed = source.getStringExtra(Constants.EXTRA_PATIENT_BED);

        if (patientId == null || patientId.trim().isEmpty()) {
            Toast.makeText(this, "Missing patient id", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        int id = parseId(patientId);
        if (id <= 0) {
            Toast.makeText(this, "Invalid patient id", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        Patient patient = patientDao.getPatientById(id);
        if (patient != null) {
            if (patientName == null || patientName.trim().isEmpty()) {
                patientName = patient.getName();
            }
            if (patientBed == null || patientBed.trim().isEmpty()) {
                patientBed = patient.getBedNumber();
            }
        } else {
            if (patientName == null || patientName.trim().isEmpty()) {
                patientName = "Unknown Patient";
            }
            if (patientBed == null || patientBed.trim().isEmpty()) {
                patientBed = "-";
            }
        }
        return true;
    }

    private void bindPatientHeader() {
        TextView tvPatientName = findViewById(R.id.tvPatientName);
        TextView tvPatientMeta = findViewById(R.id.tvPatientMeta);

        if (tvPatientName != null) {
            tvPatientName.setText(patientName);
        }
        if (tvPatientMeta != null) {
            tvPatientMeta.setText(patientId + " • Bed " + patientBed);
        }
    }

    private void setupMetricSpinner() {
        Spinner spinnerMetric = findViewById(R.id.spinnerMetric);
        if (spinnerMetric == null) {
            return;
        }

        List<String> metrics = new ArrayList<>();
        metrics.add("All Vitals");
        metrics.add("Heart Rate");
        metrics.add("SpO2");
        metrics.add("Blood Pressure");
        metrics.add("Respiratory Rate");
        metrics.add("Temperature");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                metrics
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetric.setAdapter(adapter);
    }

    private void loadVitalHistoryFromDatabase() {
        vitalHistory.clear();
        int id = parseId(patientId);
        if (id > 0) {
            vitalHistory.addAll(vitalDao.getVitalsByPatientId(id));
        }
    }

    private void setupReadingRecyclerView() {
        RecyclerView rvRecentReadings = findViewById(R.id.rvRecentReadings);
        if (rvRecentReadings == null) {
            return;
        }

        rvRecentReadings.setLayoutManager(new LinearLayoutManager(this));
        rvRecentReadings.setAdapter(new VitalHistoryAdapter(vitalHistory));
    }

    private void setupClickActions() {
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        ImageView ivExport = findViewById(R.id.ivExport);
        if (ivExport != null) {
            ivExport.setOnClickListener(v ->
                    Toast.makeText(this, "CSV/PDF export will be added with backend layer", Toast.LENGTH_SHORT).show()
            );
        }

        Button btnViewFullHistory = findViewById(R.id.btnViewFullHistory);
        if (btnViewFullHistory != null) {
            btnViewFullHistory.setOnClickListener(v ->
                    Toast.makeText(this, "Loaded " + vitalHistory.size() + " local readings", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setSelectedItemId(R.id.nav_history);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.nav_alerts) {
                startActivity(new Intent(this, AlertsActivity.class));
                return true;
            } else if (itemId == R.id.nav_history) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
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
}
