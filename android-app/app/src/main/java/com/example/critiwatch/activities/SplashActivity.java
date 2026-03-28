package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        View root = findViewById(R.id.main);
        final int basePaddingLeft = root.getPaddingLeft();
        final int basePaddingTop = root.getPaddingTop();
        final int basePaddingRight = root.getPaddingRight();
        final int basePaddingBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    basePaddingLeft + systemBars.left,
                    basePaddingTop + systemBars.top,
                    basePaddingRight + systemBars.right,
                    basePaddingBottom + systemBars.bottom
            );
            return insets;
        });

        Button btnContinue = findViewById(R.id.btnContinue);
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                Toast.makeText(this, "Opening login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        } else {
            Toast.makeText(
                    this,
                    "Missing view id: btnContinue in activity_splash.xml",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
