package com.example.critiwatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
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

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> navigateToDashboard());
        } else {
            Toast.makeText(this, "Missing view id: ivBack", Toast.LENGTH_LONG).show();
        }

        ImageView ivEditProfile = findViewById(R.id.ivEditProfile);
        if (ivEditProfile != null) {
            ivEditProfile.setOnClickListener(v -> showEditProfileDialog());
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

    private void bindSessionData() {
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userRole = sessionManager.getUserRole();

        TextView tvProfileName = findViewById(R.id.tvProfileName);
        if (tvProfileName != null && !userName.isEmpty()) {
            tvProfileName.setText(userName);
        }

        TextView tvProfileRole = findViewById(R.id.tvProfileRole);
        if (tvProfileRole != null) {
            if (!userRole.isEmpty() && !userEmail.isEmpty()) {
                tvProfileRole.setText(userRole + " • " + userEmail);
            } else if (!userRole.isEmpty()) {
                tvProfileRole.setText(userRole);
            } else if (!userEmail.isEmpty()) {
                tvProfileRole.setText(userEmail);
            }
        }

        TextView tvProfileEmail = findViewById(R.id.tvProfileEmail);
        if (tvProfileEmail != null && !userEmail.isEmpty()) {
            tvProfileEmail.setText(userEmail);
        }

        TextView tvAvatarPlaceholder = findViewById(R.id.tvAvatarPlaceholder);
        if (tvAvatarPlaceholder != null && !userName.isEmpty()) {
            tvAvatarPlaceholder.setText(buildInitials(userName));
        }
    }

    private void showEditProfileDialog() {
        int sidePadding = dpToPx(20);
        int spacing = dpToPx(12);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(sidePadding, spacing, sidePadding, 0);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        EditText etName = new EditText(this);
        etName.setHint("Full name");
        etName.setText(sessionManager.getUserName());
        etName.setSingleLine(true);
        container.addView(etName);

        EditText etEmail = new EditText(this);
        etEmail.setHint("Email");
        etEmail.setText(sessionManager.getUserEmail());
        etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmail.setSingleLine(true);
        LinearLayout.LayoutParams emailParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        emailParams.topMargin = spacing;
        container.addView(etEmail, emailParams);

        EditText etRole = new EditText(this);
        etRole.setHint("Role");
        etRole.setText(sessionManager.getUserRole());
        etRole.setSingleLine(true);
        LinearLayout.LayoutParams roleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        roleParams.topMargin = spacing;
        container.addView(etRole, roleParams);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(container)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText() == null ? "" : etName.getText().toString().trim();
            String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
            String role = etRole.getText() == null ? "" : etRole.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name is required");
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                return;
            }
            if (role.isEmpty()) {
                etRole.setError("Role is required");
                return;
            }

            sessionManager.createLoginSession(name, email, role);
            bindSessionData();
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }));

        dialog.show();
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

    private void openLoginAndClearTask() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}
