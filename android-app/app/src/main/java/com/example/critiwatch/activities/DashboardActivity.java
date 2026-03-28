package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    public static final String EXTRA_PATIENT_ID = "patient_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnAddPatient = findViewById(R.id.btnAddPatient);
        if (btnAddPatient != null) {
            btnAddPatient.setOnClickListener(v -> {
                Toast.makeText(this, "Opening Add Patient", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AddPatientActivity.class));
            });
        } else {
            Toast.makeText(this, "Missing view id: btnAddPatient", Toast.LENGTH_LONG).show();
        }

        ImageView ivProfile = findViewById(R.id.ivProfile);
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> {
                Toast.makeText(this, "Opening Profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ProfileActivity.class));
            });
        } else {
            Toast.makeText(this, "Missing view id: ivProfile", Toast.LENGTH_LONG).show();
        }

        ImageView ivSettings = findViewById(R.id.ivSettings);
        if (ivSettings != null) {
            ivSettings.setOnClickListener(v -> {
                Toast.makeText(this, "Opening Settings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SettingsActivity.class));
            });
        } else {
            Toast.makeText(this, "Missing view id: ivSettings", Toast.LENGTH_LONG).show();
        }

        setupPatientListNavigation();

        TextView tvPatientListHeader = findViewById(R.id.tvLabelPatientList);
        if (tvPatientListHeader != null) {
            tvPatientListHeader.setOnClickListener(v -> openPatientDetail("P102"));
        }

        setupBottomNavigation();
    }

    private void setupPatientListNavigation() {
        RecyclerView rvPatients = findViewById(R.id.rvPatients);
        if (rvPatients == null) {
            Toast.makeText(this, "Missing view id: rvPatients", Toast.LENGTH_LONG).show();
            return;
        }

        GestureDetector gestureDetector = new GestureDetector(
                this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent e) {
                        return true;
                    }
                }
        );

        rvPatients.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    openPatientDetail("P102");
                    return true;
                }
                return false;
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            Toast.makeText(this, "Missing view id: bottomNavigation", Toast.LENGTH_LONG).show();
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

    private void openPatientDetail(String patientId) {
        Intent intent = new Intent(this, PatientDetailActivity.class);
        intent.putExtra(EXTRA_PATIENT_ID, patientId);
        Toast.makeText(this, "Opening Patient Detail: " + patientId, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
