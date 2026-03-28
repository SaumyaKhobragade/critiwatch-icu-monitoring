package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> navigateToDashboard());
        } else {
            Toast.makeText(this, "Missing view id: ivBack", Toast.LENGTH_LONG).show();
        }

        ImageView ivEditProfile = findViewById(R.id.ivEditProfile);
        if (ivEditProfile != null) {
            ivEditProfile.setOnClickListener(v ->
                    Toast.makeText(this, "Edit profile not implemented yet", Toast.LENGTH_SHORT).show()
            );
        }

        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v ->
                    Toast.makeText(this, "Logout flow pending auth integration", Toast.LENGTH_SHORT).show()
            );
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setSelectedItemId(R.id.nav_settings);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                navigateToDashboard();
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

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
