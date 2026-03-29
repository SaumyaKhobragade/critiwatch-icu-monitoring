package com.example.critiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.critiwatch.utils.SystemUiUtils;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1500L;
    private final Handler splashHandler = new Handler(Looper.getMainLooper());
    private final Runnable splashNavigationRunnable = this::navigateNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        SystemUiUtils.applySystemBarStyling(this);

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
            btnContinue.setOnClickListener(v -> navigateNext());
        } else {
            Toast.makeText(
                    this,
                    "Missing view id: btnContinue in activity_splash.xml",
                    Toast.LENGTH_LONG
            ).show();
        }

        splashHandler.postDelayed(splashNavigationRunnable, SPLASH_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        splashHandler.removeCallbacks(splashNavigationRunnable);
        super.onDestroy();
    }

    private void navigateNext() {
        splashHandler.removeCallbacks(splashNavigationRunnable);
        startActivity(resolveNextDestinationIntent());
        finish();
    }

    private Intent resolveNextDestinationIntent() {
        // TODO: Replace with SharedPreferences-backed session check after auth state is persisted.
        // if (isUserLoggedIn) return new Intent(this, DashboardActivity.class);
        // else return new Intent(this, LoginActivity.class);
        return new Intent(this, LoginActivity.class);
    }
}
