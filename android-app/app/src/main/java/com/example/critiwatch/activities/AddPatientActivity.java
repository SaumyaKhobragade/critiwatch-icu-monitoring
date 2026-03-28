package com.example.critiwatch;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddPatientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_patient);
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

        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        } else {
            Toast.makeText(this, "Missing view id: btnCancel", Toast.LENGTH_LONG).show();
        }

        Button btnRegisterPatient = findViewById(R.id.btnRegisterPatient);
        if (btnRegisterPatient != null) {
            btnRegisterPatient.setOnClickListener(v ->
                    Toast.makeText(this, "Register action pending backend/DB wiring", Toast.LENGTH_SHORT).show()
            );
        }

        View btnScanWristband = findViewById(R.id.btnScanWristband);
        if (btnScanWristband != null) {
            btnScanWristband.setOnClickListener(v ->
                    Toast.makeText(this, "Wristband scanner not implemented yet", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
