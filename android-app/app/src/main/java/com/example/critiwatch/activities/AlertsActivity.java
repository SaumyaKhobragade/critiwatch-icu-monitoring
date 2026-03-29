package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.critiwatch.adapters.AlertAdapter;
import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.SystemUiUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {

    private final List<AlertItem> mockAlerts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alerts);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeMockAlerts();
        setupAlertRecyclerView();
        bindHeaderCount();
        setupBottomNavigation();
    }

    private void setupAlertRecyclerView() {
        RecyclerView rvAlerts = findViewById(R.id.rvAlerts);
        if (rvAlerts == null) {
            return;
        }

        rvAlerts.setLayoutManager(new LinearLayoutManager(this));
        AlertAdapter adapter = new AlertAdapter(mockAlerts, new AlertAdapter.OnAlertActionListener() {
            @Override
            public void onAlertSelected(AlertItem alertItem) {
                openAlertDetail(alertItem);
            }

            @Override
            public void onOpenPatientFile(AlertItem alertItem) {
                openPatientDetail(alertItem);
            }

            @Override
            public void onMarkResolved(AlertItem alertItem) {
                Toast.makeText(
                        AlertsActivity.this,
                        "Marked resolved: " + alertItem.getId(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        rvAlerts.setAdapter(adapter);
    }

    private void initializeMockAlerts() {
        mockAlerts.clear();
        mockAlerts.add(
                new AlertItem(
                        "ALT-001",
                        "Low SpO2",
                        Constants.RISK_CRITICAL,
                        "86",
                        "%",
                        "2m ago",
                        "SpO2 dropped below 88% for over 4 minutes. Immediate bedside assessment advised.",
                        "P102",
                        "Sarah Johnson",
                        64,
                        "Female",
                        "ICU-04",
                        Constants.RISK_CRITICAL,
                        92
                )
        );
        mockAlerts.add(
                new AlertItem(
                        "ALT-002",
                        "High Heart Rate",
                        Constants.RISK_WARNING,
                        "124",
                        "BPM",
                        "5m ago",
                        "Sustained tachycardia observed with increasing trend in last 15 minutes.",
                        "P118",
                        "Michael Ross",
                        57,
                        "Male",
                        "ICU-01",
                        Constants.RISK_WARNING,
                        78
                )
        );
        mockAlerts.add(
                new AlertItem(
                        "ALT-003",
                        "Low Blood Pressure",
                        Constants.RISK_CRITICAL,
                        "82/50",
                        "mmHg",
                        "9m ago",
                        "Hypotensive episode detected. Consider fluid challenge and sepsis protocol review.",
                        "P130",
                        "Daniel Kim",
                        71,
                        "Male",
                        "ICU-02",
                        Constants.RISK_WARNING,
                        85
                )
        );
        mockAlerts.add(
                new AlertItem(
                        "ALT-004",
                        "Prediction Alert",
                        Constants.RISK_WARNING,
                        "79",
                        "%",
                        "12m ago",
                        "Model indicates elevated deterioration risk in next 4 hours due to HR and RR trend.",
                        "P137",
                        "Elena Garcia",
                        59,
                        "Female",
                        "ICU-09",
                        Constants.RISK_STABLE,
                        79
                )
        );
    }

    private void bindHeaderCount() {
        TextView tvAlertCountBadge = findViewById(R.id.tvAlertCountBadge);
        if (tvAlertCountBadge != null) {
            tvAlertCountBadge.setText(mockAlerts.size() + " Active");
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setSelectedItemId(R.id.nav_alerts);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.nav_alerts) {
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

    private void openAlertDetail(AlertItem alertItem) {
        Intent intent = new Intent(this, NotificationDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ALERT_ID, alertItem.getId());
        intent.putExtra(Constants.EXTRA_ALERT_TYPE, alertItem.getType());
        intent.putExtra(Constants.EXTRA_ALERT_SEVERITY, alertItem.getSeverity());
        intent.putExtra(Constants.EXTRA_ALERT_VALUE, alertItem.getValue());
        intent.putExtra(Constants.EXTRA_ALERT_UNIT, alertItem.getUnit());
        intent.putExtra(Constants.EXTRA_ALERT_TIMESTAMP, alertItem.getTimestamp());
        intent.putExtra(Constants.EXTRA_ALERT_DESCRIPTION, alertItem.getDescription());
        intent.putExtra(Constants.EXTRA_PREDICTION_CONFIDENCE, alertItem.getPredictionConfidence());

        intent.putExtra(Constants.EXTRA_PATIENT_ID, alertItem.getPatientId());
        intent.putExtra(Constants.EXTRA_PATIENT_NAME, alertItem.getPatientName());
        intent.putExtra(Constants.EXTRA_PATIENT_AGE, alertItem.getPatientAge());
        intent.putExtra(Constants.EXTRA_PATIENT_SEX, alertItem.getPatientSex());
        intent.putExtra(Constants.EXTRA_PATIENT_BED, alertItem.getPatientBed());
        intent.putExtra(Constants.EXTRA_PATIENT_RISK, alertItem.getPatientRisk());
        startActivity(intent);
    }

    private void openPatientDetail(AlertItem alertItem) {
        Intent intent = new Intent(this, PatientDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PATIENT_ID, alertItem.getPatientId());
        intent.putExtra(Constants.EXTRA_PATIENT_NAME, alertItem.getPatientName());
        intent.putExtra(Constants.EXTRA_PATIENT_AGE, alertItem.getPatientAge());
        intent.putExtra(Constants.EXTRA_PATIENT_SEX, alertItem.getPatientSex());
        intent.putExtra(Constants.EXTRA_PATIENT_BED, alertItem.getPatientBed());
        intent.putExtra(Constants.EXTRA_PATIENT_RISK, alertItem.getPatientRisk());
        startActivity(intent);
    }
}
