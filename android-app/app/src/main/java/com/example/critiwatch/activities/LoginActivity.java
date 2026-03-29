package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.critiwatch.services.SessionManager;
import com.example.critiwatch.utils.SystemUiUtils;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private static final String DEMO_NAME = "Demo Clinician";
    private static final String DEMO_EMAIL = "demo@critiwatch.local";
    private static final String DEMO_ROLE = "ICU Clinician";
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            openDashboard();
            finish();
            return;
        }

        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText loginInput = etUsername;

        Button btnLogin = findViewById(R.id.btnLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> handleLogin(loginInput, etPassword));
        } else {
            Toast.makeText(this, "Missing view id: btnLogin", Toast.LENGTH_LONG).show();
        }

        View demoAccess = findViewById(R.id.demoLayout);
        if (demoAccess != null) {
            demoAccess.setOnClickListener(v -> loginAsDemoUser());
        }

        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v ->
                    Toast.makeText(this, "Forgot password not implemented yet", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void handleLogin(EditText loginInput, EditText passwordInput) {
        if (loginInput == null) {
            Toast.makeText(this, "Missing view id: etUsername or etEmail", Toast.LENGTH_LONG).show();
            return;
        }
        if (passwordInput == null) {
            Toast.makeText(this, "Missing view id: etPassword", Toast.LENGTH_LONG).show();
            return;
        }

        String identifier = loginInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        boolean hasError = false;
        if (identifier.isEmpty()) {
            loginInput.setError("Enter username or email");
            hasError = true;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Enter password");
            hasError = true;
        }
        if (hasError) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userName = buildDisplayName(identifier);
        String userEmail = buildEmail(identifier);
        String userRole = "ICU Clinician";

        sessionManager.createLoginSession(userName, userEmail, userRole);
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        openDashboard();
        finish();
    }

    private void loginAsDemoUser() {
        sessionManager.createLoginSession(DEMO_NAME, DEMO_EMAIL, DEMO_ROLE);
        Toast.makeText(this, "Demo login successful", Toast.LENGTH_SHORT).show();
        openDashboard();
        finish();
    }

    private void openDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private String buildDisplayName(String identifier) {
        if (identifier.contains("@")) {
            String localPart = identifier.substring(0, identifier.indexOf('@'));
            return titleCase(localPart.replace('.', ' ').replace('_', ' ').replace('-', ' '));
        }
        return titleCase(identifier);
    }

    private String buildEmail(String identifier) {
        if (identifier.contains("@")) {
            return identifier.toLowerCase(Locale.US);
        }
        return identifier.toLowerCase(Locale.US) + "@hospital.local";
    }

    private String titleCase(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "ICU User";
        }

        String[] parts = value.trim().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            String lower = part.toLowerCase(Locale.US);
            builder.append(Character.toUpperCase(lower.charAt(0)));
            if (lower.length() > 1) {
                builder.append(lower.substring(1));
            }
            builder.append(' ');
        }
        return builder.toString().trim();
    }
}
