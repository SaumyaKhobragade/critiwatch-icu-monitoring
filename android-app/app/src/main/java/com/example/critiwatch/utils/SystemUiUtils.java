package com.example.critiwatch.utils;

import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.critiwatch.R;

public final class SystemUiUtils {

    private SystemUiUtils() {
    }

    public static void applySystemBarStyling(AppCompatActivity activity) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.surface_secondary));
        window.setNavigationBarColor(ContextCompat.getColor(activity, R.color.surface_card));

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }
    }
}
