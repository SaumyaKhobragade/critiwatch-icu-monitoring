package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

    private static final String FILTER_ALL = "all";
    private static final String FILTER_CRITICAL = "critical";
    private static final String FILTER_WARNING = "warning";
    private static final String FILTER_ACKNOWLEDGED = "acknowledged";

    private final List<AlertItem> allAlerts = new ArrayList<>();
    private final List<AlertItem> filteredAlerts = new ArrayList<>();
    private AlertRepository alertRepository;
    private AlertAdapter alertAdapter;
    private String currentFilter = FILTER_ALL;

    private TextView filterAll;
    private TextView filterCritical;
    private TextView filterWarning;
    private TextView filterAcknowledged;

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
        setupFilterChips();
        loadAlertsFromDatabase();
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
        alertAdapter = new AlertAdapter(filteredAlerts, new AlertAdapter.OnAlertActionListener() {
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
                if (alertItem.isAcknowledged()) {
                    Toast.makeText(AlertsActivity.this, "Alert already acknowledged", Toast.LENGTH_SHORT).show();
                    return;
                }

                int alertId = parseId(alertItem.getId());
                if (alertId <= 0) {
                    Toast.makeText(AlertsActivity.this, "Invalid alert id", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean success = alertRepository.acknowledgeAlert(alertId);
                Toast.makeText(AlertsActivity.this,
                        success ? "Alert resolved" : "Unable to resolve alert",
                        Toast.LENGTH_SHORT).show();
                loadAlertsFromDatabase();
            }
        });
        rvAlerts.setAdapter(alertAdapter);
    }

    private void setupFilterChips() {
        filterAll = findViewById(R.id.filterAll);
        filterCritical = findViewById(R.id.filterCritical);
        filterWarning = findViewById(R.id.filterWarning);
        filterAcknowledged = findViewById(R.id.filterAcknowledged);

        if (filterAll != null) {
            filterAll.setOnClickListener(v -> setFilter(FILTER_ALL));
        }
        if (filterCritical != null) {
            filterCritical.setOnClickListener(v -> setFilter(FILTER_CRITICAL));
        }
        if (filterWarning != null) {
            filterWarning.setOnClickListener(v -> setFilter(FILTER_WARNING));
        }
        if (filterAcknowledged != null) {
            filterAcknowledged.setOnClickListener(v -> setFilter(FILTER_ACKNOWLEDGED));
        }

        updateFilterChipStyles();
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        applyAlertFilter();
    }

    private void loadAlertsFromDatabase() {
        allAlerts.clear();
        allAlerts.addAll(alertRepository.getAllAlerts());
        applyAlertFilter();
    }

    private void applyAlertFilter() {
        filteredAlerts.clear();
        for (AlertItem alert : allAlerts) {
            if (matchesCurrentFilter(alert)) {
                filteredAlerts.add(alert);
            }
        }

        if (alertAdapter != null) {
            alertAdapter.notifyDataSetChanged();
        }
        bindEmptyState();
        updateFilterChipStyles();
        bindHeaderCount();
    }

    private void bindEmptyState() {
        RecyclerView rvAlerts = findViewById(R.id.rvAlerts);
        TextView tvEmptyAlerts = findViewById(R.id.tvEmptyAlerts);
        boolean isEmpty = filteredAlerts.isEmpty();
        if (rvAlerts != null) {
            rvAlerts.setVisibility(isEmpty ? android.view.View.GONE : android.view.View.VISIBLE);
        }
        if (tvEmptyAlerts != null) {
            tvEmptyAlerts.setVisibility(isEmpty ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private boolean matchesCurrentFilter(AlertItem alert) {
        if (FILTER_CRITICAL.equals(currentFilter)) {
            return Constants.RISK_CRITICAL.equalsIgnoreCase(alert.getSeverity());
        }
        if (FILTER_WARNING.equals(currentFilter)) {
            return Constants.RISK_WARNING.equalsIgnoreCase(alert.getSeverity());
        }
        if (FILTER_ACKNOWLEDGED.equals(currentFilter)) {
            return alert.isAcknowledged();
        }
        return true;
    }

    private void updateFilterChipStyles() {
        styleFilterChip(filterAll, FILTER_ALL.equals(currentFilter));
        styleFilterChip(filterCritical, FILTER_CRITICAL.equals(currentFilter));
        styleFilterChip(filterWarning, FILTER_WARNING.equals(currentFilter));
        styleFilterChip(filterAcknowledged, FILTER_ACKNOWLEDGED.equals(currentFilter));
    }

    private void styleFilterChip(TextView chip, boolean selected) {
        if (chip == null) {
            return;
        }

        chip.setBackgroundResource(selected ? R.drawable.bg_button_primary : R.drawable.bg_chip_neutral);
        chip.setTextColor(ContextCompat.getColor(this, selected ? R.color.inverse_text : R.color.text_secondary));
    }

    private void bindHeaderCount() {
        TextView tvAlertCountBadge = findViewById(R.id.tvAlertCountBadge);
        if (tvAlertCountBadge != null) {
            int activeCount = 0;
            for (AlertItem alert : allAlerts) {
                if (!alert.isAcknowledged()) {
                    activeCount++;
                }
            }
            tvAlertCountBadge.setText(activeCount + " Active");
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
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void openAlertDetail(AlertItem alertItem) {
        int alertId = parseId(alertItem.getId());
        if (alertId <= 0) {
            Toast.makeText(this, "Invalid alert details", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, NotificationDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ALERT_ID, String.valueOf(alertId));
        startActivity(intent);
    }

    private void openPatientDetail(AlertItem alertItem) {
        Intent intent = new Intent(this, PatientDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PATIENT_ID, alertItem.getPatientId());
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
