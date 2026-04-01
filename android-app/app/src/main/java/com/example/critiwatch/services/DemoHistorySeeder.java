package com.example.critiwatch.services;

import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public final class DemoHistorySeeder {

    private static final int MIN_HR = 20;
    private static final int MAX_HR = 240;
    private static final int MIN_SPO2 = 50;
    private static final int MAX_SPO2 = 100;
    private static final int MIN_SYS = 40;
    private static final int MAX_SYS = 260;
    private static final int MIN_DIA = 20;
    private static final int MAX_DIA = 180;
    private static final int MIN_RR = 5;
    private static final int MAX_RR = 80;
    private static final double MIN_TEMP = 33.0d;
    private static final double MAX_TEMP = 43.0d;

    private DemoHistorySeeder() {
    }

    public static List<VitalSign> generatePastVitals(
            String patientId,
            VitalSign currentVital,
            String riskLevel,
            int recordCount
    ) {
        List<VitalSign> generated = new ArrayList<>();
        if (currentVital == null || recordCount <= 0) {
            return generated;
        }

        int safeCount = Math.max(8, Math.min(12, recordCount));
        Date baseTime = DateTimeUtils.parse(currentVital.getTimestamp());
        if (baseTime == null) {
            baseTime = new Date();
        }

        long seed = (patientId == null ? 0L : patientId.hashCode()) ^ baseTime.getTime();
        Random random = new Random(seed);

        double severityFactor = resolveSeverityFactor(riskLevel);

        double oldestHr = buildOldestValue(
                currentVital.getHeartRate(),
                60d,
                100d,
                6d * severityFactor,
                14d * severityFactor,
                random
        );
        double oldestSpo2 = buildOldestValue(
                currentVital.getSpo2(),
                95d,
                100d,
                1.2d * severityFactor,
                4.0d * severityFactor,
                random
        );
        double oldestSys = buildOldestValue(
                currentVital.getSystolicBp(),
                105d,
                125d,
                5d * severityFactor,
                14d * severityFactor,
                random
        );
        double oldestDia = buildOldestValue(
                currentVital.getDiastolicBp(),
                65d,
                85d,
                3d * severityFactor,
                10d * severityFactor,
                random
        );
        double oldestRr = buildOldestValue(
                currentVital.getRespiratoryRate(),
                12d,
                20d,
                2d * severityFactor,
                6d * severityFactor,
                random
        );
        double oldestTemp = buildOldestValue(
                currentVital.getTemperature(),
                36.4d,
                37.4d,
                0.2d * severityFactor,
                0.8d * severityFactor,
                random
        );

        for (int hourOffset = safeCount; hourOffset >= 1; hourOffset--) {
            int chronologicalIndex = safeCount - hourOffset + 1;
            double progress = chronologicalIndex / (double) (safeCount + 1);

            int heartRate = clampInt(
                    interpolateWithNoise(
                            oldestHr,
                            currentVital.getHeartRate(),
                            progress,
                            1.6d * severityFactor,
                            random
                    ),
                    MIN_HR,
                    MAX_HR
            );
            int spo2 = clampInt(
                    interpolateWithNoise(
                            oldestSpo2,
                            currentVital.getSpo2(),
                            progress,
                            0.6d * severityFactor,
                            random
                    ),
                    MIN_SPO2,
                    MAX_SPO2
            );
            int systolic = clampInt(
                    interpolateWithNoise(
                            oldestSys,
                            currentVital.getSystolicBp(),
                            progress,
                            1.5d * severityFactor,
                            random
                    ),
                    MIN_SYS,
                    MAX_SYS
            );
            int diastolic = clampInt(
                    interpolateWithNoise(
                            oldestDia,
                            currentVital.getDiastolicBp(),
                            progress,
                            1.2d * severityFactor,
                            random
                    ),
                    MIN_DIA,
                    MAX_DIA
            );
            if (diastolic >= systolic - 8) {
                diastolic = Math.max(MIN_DIA, systolic - 8 - random.nextInt(6));
            }

            int respiratoryRate = clampInt(
                    interpolateWithNoise(
                            oldestRr,
                            currentVital.getRespiratoryRate(),
                            progress,
                            0.8d * severityFactor,
                            random
                    ),
                    MIN_RR,
                    MAX_RR
            );
            double temperature = clampDouble(
                    interpolateWithNoise(
                            oldestTemp,
                            currentVital.getTemperature(),
                            progress,
                            0.08d * severityFactor,
                            random
                    ),
                    MIN_TEMP,
                    MAX_TEMP
            );

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(baseTime);
            calendar.add(Calendar.HOUR_OF_DAY, -hourOffset);
            String recordedAt = DateTimeUtils.format(calendar.getTime());

            VitalSign historical = new VitalSign(
                    null,
                    patientId,
                    heartRate,
                    spo2,
                    systolic,
                    diastolic,
                    respiratoryRate,
                    temperature,
                    currentVital.getHeightCm(),
                    currentVital.getWeightKg(),
                    recordedAt
            );
            generated.add(historical);
        }

        return generated;
    }

    private static double resolveSeverityFactor(String riskLevel) {
        if (Constants.RISK_CRITICAL.equalsIgnoreCase(riskLevel)) {
            return 1.6d;
        }
        if (Constants.RISK_WARNING.equalsIgnoreCase(riskLevel)) {
            return 1.15d;
        }
        return 0.65d;
    }

    private static double buildOldestValue(
            double current,
            double normalLow,
            double normalHigh,
            double mildDelta,
            double severeDelta,
            Random random
    ) {
        if (current > normalHigh) {
            double improvement = Math.max(mildDelta, Math.min(severeDelta, (current - normalHigh) * 0.8d + mildDelta));
            return current - improvement;
        }
        if (current < normalLow) {
            double improvement = Math.max(mildDelta, Math.min(severeDelta, (normalLow - current) * 0.8d + mildDelta));
            return current + improvement;
        }
        return current + randomInRange(random, -mildDelta, mildDelta);
    }

    private static double interpolateWithNoise(double start, double end, double progress, double noiseAmplitude, Random random) {
        double lerp = start + (end - start) * progress;
        return lerp + randomInRange(random, -noiseAmplitude, noiseAmplitude);
    }

    private static double randomInRange(Random random, double min, double max) {
        return min + ((max - min) * random.nextDouble());
    }

    private static int clampInt(double value, int min, int max) {
        return Math.max(min, Math.min(max, (int) Math.round(value)));
    }

    private static double clampDouble(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
