package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AlertsActivity extends AppCompatActivity {

    public static final String EXTRA_ALERT_ID = "alert_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alerts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAlertListNavigation();
        setupBottomNavigation();
    }

    private void setupAlertListNavigation() {
        RecyclerView rvAlerts = findViewById(R.id.rvAlerts);
        if (rvAlerts == null) {
            Toast.makeText(this, "Missing view id: rvAlerts", Toast.LENGTH_LONG).show();
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

        rvAlerts.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    openAlertDetail("ALT-001");
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

    private void openAlertDetail(String alertId) {
        Intent intent = new Intent(this, NotificationDetailActivity.class);
        intent.putExtra(EXTRA_ALERT_ID, alertId);
        Toast.makeText(this, "Opening Alert: " + alertId, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
