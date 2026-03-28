package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotificationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnViewPatientProfile = findViewById(R.id.btnViewPatientProfile);
        if (btnViewPatientProfile != null) {
            btnViewPatientProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, PatientDetailActivity.class);
                intent.putExtra(
                        "patient_id",
                        getIntent().getStringExtra("patient_id") != null
                                ? getIntent().getStringExtra("patient_id")
                                : "P102"
                );
                startActivity(intent);
            });
        } else {
            Toast.makeText(this, "Missing view id: btnViewPatientProfile", Toast.LENGTH_LONG).show();
        }

        Button btnAcknowledgeAlert = findViewById(R.id.btnAcknowledgeAlert);
        if (btnAcknowledgeAlert != null) {
            btnAcknowledgeAlert.setOnClickListener(v -> {
                Toast.makeText(this, "Alert acknowledged", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AlertsActivity.class));
            });
        }

        Button btnActivateResponse = findViewById(R.id.btnActivateResponse);
        if (btnActivateResponse != null) {
            btnActivateResponse.setOnClickListener(v ->
                    Toast.makeText(this, "Rapid response action is UI-only for now", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
