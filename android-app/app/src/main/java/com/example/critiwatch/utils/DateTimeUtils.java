package com.example.critiwatch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    private static final String DB_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateTimeUtils() {
    }

    public static String now() {
        return format(new Date());
    }

    public static String minutesAgo(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -Math.max(0, minutes));
        return format(calendar.getTime());
    }

    public static String format(Date date) {
        return new SimpleDateFormat(DB_TIMESTAMP_PATTERN, Locale.US).format(date);
    }

    public static Date parse(String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat(DB_TIMESTAMP_PATTERN, Locale.US).parse(timestamp);
        } catch (ParseException ignored) {
            return null;
        }
    }

    public static String toRelativeTime(String timestamp) {
        Date recordedAt = parse(timestamp);
        if (recordedAt == null) {
            return timestamp == null ? "" : timestamp;
        }

        long diffMillis = Math.max(0L, System.currentTimeMillis() - recordedAt.getTime());
        long minutes = diffMillis / (60L * 1000L);
        if (minutes < 1L) {
            return "Just now";
        }
        if (minutes < 60L) {
            return minutes + " min ago";
        }

        long hours = minutes / 60L;
        if (hours < 24L) {
            return hours + "h ago";
        }

        long days = hours / 24L;
        return days + "d ago";
    }
}
