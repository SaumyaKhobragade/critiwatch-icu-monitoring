package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.critiwatch.services.SessionManager;
import com.example.critiwatch.utils.SystemUiUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLoginAndClearTask();
            return;
        }

        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindSessionData();

        TextView btnViewProfileLink = findViewById(R.id.btnViewProfileLink);
        if (btnViewProfileLink != null) {
            btnViewProfileLink.setOnClickListener(v -> openProfileScreen());
        }

        Button btnSettingsLogout = findViewById(R.id.btnSettingsLogout);
        if (btnSettingsLogout != null) {
            btnSettingsLogout.setOnClickListener(v -> logoutUser());
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            Toast.makeText(this, "Missing view id: bottomNavigation", Toast.LENGTH_LONG).show();
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
            } else if (itemId == R.id.nav_settings) {
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

    private void openProfileScreen() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void bindSessionData() {
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userRole = sessionManager.getUserRole();

        TextView tvSettingsProfileName = findViewById(R.id.tvSettingsProfileName);
        if (tvSettingsProfileName != null && !userName.isEmpty()) {
            tvSettingsProfileName.setText(userName);
        }

        TextView tvSettingsProfileMeta = findViewById(R.id.tvSettingsProfileMeta);
        if (tvSettingsProfileMeta != null) {
            if (!userRole.isEmpty() && !userEmail.isEmpty()) {
                tvSettingsProfileMeta.setText(userRole + " • " + userEmail);
            } else if (!userRole.isEmpty()) {
                tvSettingsProfileMeta.setText(userRole);
            } else if (!userEmail.isEmpty()) {
                tvSettingsProfileMeta.setText(userEmail);
            }
        }

        TextView tvAvatarSmall = findViewById(R.id.tvAvatarSmall);
        if (tvAvatarSmall != null && !userName.isEmpty()) {
            tvAvatarSmall.setText(buildInitials(userName));
        }
    }

    private String buildInitials(String fullName) {
        String trimmed = fullName == null ? "" : fullName.trim();
        if (trimmed.isEmpty()) {
            return "CW";
        }

        String[] parts = trimmed.split("\\s+");
        String first = parts[0].substring(0, 1).toUpperCase();
        if (parts.length == 1) {
            return first;
        }
        String last = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return first + last;
    }

    private void logoutUser() {
        sessionManager.clearSession();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        openLoginAndClearTask();
    }

    private void openLoginAndClearTask() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
