package com.example.critiwatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
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

import com.example.critiwatch.models.UserProfile;
import com.example.critiwatch.repository.UserProfileRepository;
import com.example.critiwatch.services.SessionManager;
import com.example.critiwatch.utils.SystemUiUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private UserProfileRepository userProfileRepository;
    private UserProfile currentProfile;

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

        userProfileRepository = new UserProfileRepository(this);
        userProfileRepository.createDefaultProfileIfMissing();

        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindProfileData();

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> navigateToDashboard());
        }

        View editTrigger = findViewById(R.id.btnEditProfile);
        if (editTrigger == null) {
            int legacyEditId = getResources().getIdentifier("ivEditProfile", "id", getPackageName());
            if (legacyEditId != 0) {
                editTrigger = findViewById(legacyEditId);
            }
        }
        if (editTrigger != null) {
            editTrigger.setOnClickListener(v -> showEditProfileDialog());
        } else {
            Toast.makeText(this, "Missing view id: btnEditProfile or ivEditProfile", Toast.LENGTH_LONG).show();
        }

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindProfileData();
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

    private void bindProfileData() {
        currentProfile = userProfileRepository.getOrCreateProfile();
        if (currentProfile == null) {
            return;
        }

        String name = safe(currentProfile.getName(), "Demo Doctor");
        String email = safe(currentProfile.getEmail(), "doctor@critiwatch.local");
        String designation = safe(currentProfile.getDesignation(), "ICU Resident");

        setFirstExistingText(name, "tvProfileName", "tvUserName");
        setFirstExistingText(designation + " • " + email, "tvProfileRole", "tvUserRole");
        setFirstExistingText(email, "tvProfileEmail", "tvUserEmail");
        setFirstExistingText(name, "tvProfileFullNameValue");
        setFirstExistingText(designation, "tvProfileDesignationValue");
        setFirstExistingText(buildInitials(name), "tvAvatarPlaceholder");
    }

    private void showEditProfileDialog() {
        if (currentProfile == null) {
            currentProfile = userProfileRepository.getOrCreateProfile();
        }
        if (currentProfile == null) {
            Toast.makeText(this, "Unable to load profile", Toast.LENGTH_SHORT).show();
            return;
        }

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
        etName.setText(currentProfile.getName());
        etName.setSingleLine(true);
        container.addView(etName);

        EditText etEmail = new EditText(this);
        etEmail.setHint("Email");
        etEmail.setText(currentProfile.getEmail());
        etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmail.setSingleLine(true);
        LinearLayout.LayoutParams emailParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        emailParams.topMargin = spacing;
        container.addView(etEmail, emailParams);

        EditText etDesignation = new EditText(this);
        etDesignation.setHint("Designation");
        etDesignation.setText(currentProfile.getDesignation());
        etDesignation.setSingleLine(true);
        LinearLayout.LayoutParams roleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        roleParams.topMargin = spacing;
        container.addView(etDesignation, roleParams);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(container)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText() == null ? "" : etName.getText().toString().trim();
            String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
            String designation = etDesignation.getText() == null ? "" : etDesignation.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name is required");
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                return;
            }
            if (!email.contains("@")) {
                etEmail.setError("Enter a valid email");
                return;
            }
            if (designation.isEmpty()) {
                etDesignation.setError("Designation is required");
                return;
            }

            UserProfile updated = new UserProfile(
                    currentProfile.getId(),
                    name,
                    email,
                    designation,
                    currentProfile.getUpdatedAt()
            );
            boolean saved = userProfileRepository.saveProfile(updated);
            if (!saved) {
                Toast.makeText(this, "Unable to save profile", Toast.LENGTH_SHORT).show();
                return;
            }

            bindProfileData();
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }));

        dialog.show();
    }

    private void setFirstExistingText(String value, String... idNames) {
        for (String idName : idNames) {
            int viewId = getResources().getIdentifier(idName, "id", getPackageName());
            if (viewId == 0) {
                continue;
            }
            View view = findViewById(viewId);
            if (view instanceof TextView) {
                ((TextView) view).setText(value);
                return;
            }
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

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
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
