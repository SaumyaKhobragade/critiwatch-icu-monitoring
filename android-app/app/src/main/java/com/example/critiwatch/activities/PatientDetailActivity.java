package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PatientDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PATIENT_ID = "patient_id";
    public static final String EXTRA_ALERT_ID = "alert_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        } else {
            Toast.makeText(this, "Missing view id: ivBack", Toast.LENGTH_LONG).show();
        }

        Button btnViewHistory = findViewById(R.id.btnViewHistory);
        if (btnViewHistory != null) {
            btnViewHistory.setOnClickListener(v -> {
                Intent intent = new Intent(this, GraphHistoryActivity.class);
                intent.putExtra(EXTRA_PATIENT_ID, getPatientIdFromIntent());
                startActivity(intent);
            });
        } else {
            Toast.makeText(this, "Missing view id: btnViewHistory", Toast.LENGTH_LONG).show();
        }

        Button btnActivateResponse = findViewById(R.id.btnActivateResponse);
        if (btnActivateResponse != null) {
            btnActivateResponse.setOnClickListener(v -> openAlertDetail());
        }

        Button btnAcknowledgeAlert = findViewById(R.id.btnAcknowledgeAlert);
        if (btnAcknowledgeAlert != null) {
            btnAcknowledgeAlert.setOnClickListener(v ->
                    Toast.makeText(this, "Alert acknowledged (UI test)", Toast.LENGTH_SHORT).show()
            );
        }

        LinearLayout llAlertBanner = findViewById(R.id.llAlertBanner);
        if (llAlertBanner != null) {
            llAlertBanner.setOnClickListener(v -> openAlertDetail());
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
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

    private void openAlertDetail() {
        Intent intent = new Intent(this, NotificationDetailActivity.class);
        intent.putExtra(EXTRA_PATIENT_ID, getPatientIdFromIntent());
        intent.putExtra(EXTRA_ALERT_ID, "ALT-001");
        Toast.makeText(this, "Opening alert detail", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    private String getPatientIdFromIntent() {
        String patientId = getIntent().getStringExtra(EXTRA_PATIENT_ID);
        return patientId != null ? patientId : "P102";
    }
}
