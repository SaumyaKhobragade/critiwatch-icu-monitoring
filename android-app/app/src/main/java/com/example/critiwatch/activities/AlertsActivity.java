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
import com.example.critiwatch.database.DatabaseSeeder;
import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.repository.AlertRepository;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.SystemUiUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {

    private final List<AlertItem> alerts = new ArrayList<>();
    private AlertRepository alertRepository;
    private AlertAdapter alertAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alerts);
        alertRepository = new AlertRepository(this);
        DatabaseSeeder.seedIfEmpty(this);
        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAlertRecyclerView();
        loadAlertsFromDatabase();
        bindHeaderCount();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlertsFromDatabase();
    }

    private void setupAlertRecyclerView() {
        RecyclerView rvAlerts = findViewById(R.id.rvAlerts);
        if (rvAlerts == null) {
            return;
        }

        rvAlerts.setLayoutManager(new LinearLayoutManager(this));
        alertAdapter = new AlertAdapter(alerts, new AlertAdapter.OnAlertActionListener() {
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
                int alertId = parseId(alertItem.getId());
                boolean success = alertRepository.acknowledgeAlert(alertId);
                Toast.makeText(AlertsActivity.this,
                        success ? "Alert resolved" : "Unable to resolve alert",
                        Toast.LENGTH_SHORT).show();
                loadAlertsFromDatabase();
            }
        });
        rvAlerts.setAdapter(alertAdapter);
    }

    private void loadAlertsFromDatabase() {
        alerts.clear();
        alerts.addAll(alertRepository.getAllAlerts());
        if (alertAdapter != null) {
            alertAdapter.notifyDataSetChanged();
        }
        bindHeaderCount();
    }

    private void bindHeaderCount() {
        TextView tvAlertCountBadge = findViewById(R.id.tvAlertCountBadge);
        if (tvAlertCountBadge != null) {
            tvAlertCountBadge.setText(alerts.size() + " Active");
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
