package com.example.critiwatch.services;

public class ValidationService {

    private ValidationService() {
        // Utility class
    }

    public static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static Integer tryParseInt(String input) {
        if (isBlank(input)) {
            return null;
        }
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Double tryParseDouble(String input) {
        if (isBlank(input)) {
            return null;
        }
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
