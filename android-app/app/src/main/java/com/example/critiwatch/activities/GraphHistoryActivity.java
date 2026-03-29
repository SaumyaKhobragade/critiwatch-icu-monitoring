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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_graph_history);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readPatientExtras();
        bindPatientHeader();
        setupMetricSpinner();
        initializeMockVitalHistory();
        setupReadingRecyclerView();
        setupClickActions();
        setupBottomNavigation();
    }

    private void readPatientExtras() {
        Intent source = getIntent();
        patientId = source.getStringExtra(Constants.EXTRA_PATIENT_ID);
        patientName = source.getStringExtra(Constants.EXTRA_PATIENT_NAME);
        patientBed = source.getStringExtra(Constants.EXTRA_PATIENT_BED);

        if (patientId == null || patientId.trim().isEmpty()) {
            patientId = "P102";
        }
        if (patientName == null || patientName.trim().isEmpty()) {
            patientName = "Sarah Johnson";
        }
        if (patientBed == null || patientBed.trim().isEmpty()) {
            patientBed = "ICU-04";
        }
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

    private void initializeMockVitalHistory() {
        vitalHistory.clear();
        vitalHistory.add(new VitalSign("14:30:05", 132, 86, "85/55", 30, 38.6));
        vitalHistory.add(new VitalSign("14:25:10", 128, 88, "88/58", 28, 38.4));
        vitalHistory.add(new VitalSign("14:20:08", 124, 90, "92/60", 27, 38.2));
        vitalHistory.add(new VitalSign("14:15:12", 120, 91, "95/62", 25, 38.0));
        vitalHistory.add(new VitalSign("14:10:07", 116, 92, "98/64", 24, 37.8));
        vitalHistory.add(new VitalSign("14:05:06", 110, 93, "102/68", 22, 37.6));
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
                    Toast.makeText(this, "Showing recent mock vitals for now", Toast.LENGTH_SHORT).show()
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
}
