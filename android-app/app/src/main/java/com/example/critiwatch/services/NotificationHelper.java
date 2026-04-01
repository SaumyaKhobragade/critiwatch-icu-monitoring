package com.example.critiwatch.services;

import android.annotation.SuppressLint;
import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.critiwatch.PatientDetailActivity;
import com.example.critiwatch.NotificationDetailActivity;
import com.example.critiwatch.R;
import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.utils.Constants;

import java.util.Locale;

public class NotificationHelper {

    public static final String CHANNEL_ID_ALERTS = "critiwatch_alerts_channel";
    private static final String CHANNEL_NAME_ALERTS = "CritiWatch Alerts";
    private static final String CHANNEL_DESC_ALERTS = "Warning and critical ICU monitoring alerts";

    private NotificationHelper() {
    }

    public static void ensureNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        NotificationChannel existing = manager.getNotificationChannel(CHANNEL_ID_ALERTS);
        if (existing != null) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_ALERTS,
                CHANNEL_NAME_ALERTS,
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription(CHANNEL_DESC_ALERTS);
        manager.createNotificationChannel(channel);
    }

    @SuppressLint("MissingPermission")
    public static boolean showAlertNotification(Context context, AlertItem alertItem) {
        if (context == null || alertItem == null) {
            return false;
        }

        ensureNotificationChannel(context);

        if (!hasNotificationPermission(context)) {
            return false;
        }

        int notificationId = parseNotificationId(alertItem);
        String severity = safeValue(alertItem.getSeverity(), Constants.RISK_WARNING);
        String title = buildNotificationTitle(severity, alertItem.getType());
        String patientName = safeValue(alertItem.getPatientName(), "Unknown Patient");
        String description = safeValue(alertItem.getDescription(), "Risk threshold crossed.");
        String message = buildShortMessage(patientName, description);

        Intent tapIntent = buildTapIntent(context, alertItem);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
                .setSmallIcon(R.drawable.ic_nav_alerts)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(resolvePriority(alertItem.getSeverity()))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build());
            return true;
        } catch (SecurityException ignored) {
            return false;
        }
    }

    public static boolean hasNotificationPermission(Context context) {
        if (context == null) {
            return false;
        }
        if (!needsNotificationPermission()) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean needsNotificationPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    public static void requestNotificationPermission(Activity activity, int requestCode) {
        if (activity == null || !needsNotificationPermission()) {
            return;
        }
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                requestCode
        );
    }

    private static Intent buildTapIntent(Context context, AlertItem alertItem) {
        String rawAlertId = safeValue(alertItem.getId(), "");
        int parsedAlertId = parseDbId(rawAlertId);

        Intent tapIntent;
        if (parsedAlertId > 0 && !rawAlertId.isEmpty()) {
            tapIntent = new Intent(context, NotificationDetailActivity.class);
            tapIntent.putExtra(Constants.EXTRA_ALERT_ID, rawAlertId);
            tapIntent.putExtra(Constants.EXTRA_PATIENT_ID, alertItem.getPatientId());
        } else {
            tapIntent = new Intent(context, PatientDetailActivity.class);
            tapIntent.putExtra(Constants.EXTRA_PATIENT_ID, alertItem.getPatientId());
        }
        tapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return tapIntent;
    }

    private static int parseDbId(String rawId) {
        if (rawId == null || rawId.trim().isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(rawId.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private static int parseNotificationId(AlertItem alertItem) {
        if (alertItem == null) {
            return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        }

        String rawAlertId = alertItem.getId();
        if (rawAlertId != null && !rawAlertId.trim().isEmpty()) {
            try {
                return Integer.parseInt(rawAlertId.trim());
            } catch (NumberFormatException ignored) {
                // fall through to hash-based id
            }
        }

        String fallbackKey = safeValue(alertItem.getPatientId(), "")
                + "|"
                + safeValue(alertItem.getType(), "")
                + "|"
                + safeValue(alertItem.getTimestamp(), "");
        int hash = Math.abs(fallbackKey.hashCode());
        if (hash == 0) {
            return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        }
        return hash;
    }

    private static String safeValue(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }

    private static int resolvePriority(String severity) {
        if (severity != null && severity.trim().equalsIgnoreCase(Constants.RISK_CRITICAL)) {
            return NotificationCompat.PRIORITY_MAX;
        }
        return NotificationCompat.PRIORITY_HIGH;
    }

    private static String buildShortMessage(String patientName, String description) {
        String compact = description == null ? "" : description.trim();
        if (compact.length() > 100) {
            compact = compact.substring(0, 97) + "...";
        }
        return patientName + " • " + compact;
    }

    private static String buildNotificationTitle(String severity, String alertType) {
        if (Constants.RISK_CRITICAL.equalsIgnoreCase(severity)) {
            return "Critical Alert: Deterioration Risk";
        }
        if (Constants.RISK_WARNING.equalsIgnoreCase(severity)) {
            return "Warning Alert: Patient Needs Attention";
        }
        String fallbackType = safeValue(alertType, "Prediction Alert");
        return toTitleCase(severity) + " Alert: " + fallbackType;
    }

    private static String toTitleCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Alert";
        }
        String normalized = input.trim().toLowerCase(Locale.US);
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }
}
