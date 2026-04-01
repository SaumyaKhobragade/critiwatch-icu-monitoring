package com.example.critiwatch.database;

import android.content.Context;

import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.Prediction;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.services.DemoHistorySeeder;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DatabaseSeeder {

    private static final int TARGET_HISTORY_RECORDS = 10;
    private static final int MIN_TOTAL_VITAL_ROWS = 9;

    private DatabaseSeeder() {
    }

    public static void seedIfEmpty(Context context) {
        PatientDao patientDao = new PatientDao(context);
        List<Patient> existing = patientDao.getAllPatients();
        if (!existing.isEmpty()) {
            return;
        }
        loadDemoPatientsInternal(context, true);
    }

    public static void backfillExistingDemoPatients(Context context) {
        loadDemoPatientsInternal(context, false);
    }

    public static DemoLoadResult loadDemoPatientsIfMissing(Context context) {
        return loadDemoPatientsInternal(context, true);
    }

    private static DemoLoadResult loadDemoPatientsInternal(Context context, boolean allowInsertMissingPatients) {
        PatientDao patientDao = new PatientDao(context);
        VitalDao vitalDao = new VitalDao(context);
        PredictionDao predictionDao = new PredictionDao(context);
        AlertDao alertDao = new AlertDao(context);

        List<Patient> allPatients = patientDao.getAllPatients();
        List<DemoPatientSpec> demoSpecs = getDemoSpecs();

        int insertedPatients = 0;
        int insertedVitals = 0;
        int insertedPredictions = 0;
        int insertedAlerts = 0;
        boolean allDemoPatientsAlreadyPresent = true;

        for (DemoPatientSpec spec : demoSpecs) {
            Patient existingDemoPatient = findDemoPatient(allPatients, spec.name, spec.bedNumber);
            if (existingDemoPatient == null) {
                allDemoPatientsAlreadyPresent = false;
                if (!allowInsertMissingPatients) {
                    continue;
                }

                long patientId = patientDao.insertPatient(spec.toPatient());
                if (patientId <= 0L) {
                    continue;
                }

                insertedPatients++;
                allPatients.add(new Patient(
                        String.valueOf(patientId),
                        spec.name,
                        spec.age,
                        spec.sex,
                        spec.ward,
                        spec.bedNumber,
                        spec.riskLevel,
                        DateTimeUtils.minutesAgo(spec.createdMinutesAgo)
                ));

                insertedVitals += insertCurrentAndHistoricalVitals(vitalDao, patientId, spec);
                insertedPredictions += ensurePredictionExists(predictionDao, (int) patientId, spec);
                insertedAlerts += ensureAlertExists(alertDao, (int) patientId, spec);
                continue;
            }

            int patientId = parseId(existingDemoPatient.getId());
            if (patientId <= 0) {
                continue;
            }

            if (!equalsIgnoreCase(spec.riskLevel, existingDemoPatient.getRiskLevel())) {
                patientDao.updatePatientRiskLevel(patientId, spec.riskLevel);
            }

            insertedVitals += ensureHistoricalVitals(vitalDao, patientId, spec);
            insertedPredictions += ensurePredictionExists(predictionDao, patientId, spec);
            insertedAlerts += ensureAlertExists(alertDao, patientId, spec);
        }

        boolean skippedAsAlreadyLoaded = allDemoPatientsAlreadyPresent
                && insertedPatients == 0
                && insertedVitals == 0
                && insertedPredictions == 0
                && insertedAlerts == 0;

        return new DemoLoadResult(
                insertedPatients,
                insertedVitals,
                insertedPredictions,
                insertedAlerts,
                skippedAsAlreadyLoaded
        );
    }

    private static int insertCurrentAndHistoricalVitals(VitalDao vitalDao, long patientId, DemoPatientSpec spec) {
        if (patientId <= 0L) {
            return 0;
        }

        String patientIdText = String.valueOf(patientId);
        VitalSign latest = spec.toCurrentVital(patientIdText);
        int inserted = vitalDao.insertVital(latest) > 0L ? 1 : 0;

        List<VitalSign> olderVitals = DemoHistorySeeder.generatePastVitals(
                patientIdText,
                latest,
                spec.riskLevel,
                TARGET_HISTORY_RECORDS
        );
        for (VitalSign vital : olderVitals) {
            if (vitalDao.insertVital(vital) > 0L) {
                inserted++;
            }
        }
        return inserted;
    }

    private static int ensureHistoricalVitals(VitalDao vitalDao, int patientId, DemoPatientSpec spec) {
        List<VitalSign> existingVitals = vitalDao.getVitalsByPatientId(patientId);
        if (existingVitals.isEmpty()) {
            return insertCurrentAndHistoricalVitals(vitalDao, patientId, spec);
        }
        if (existingVitals.size() >= MIN_TOTAL_VITAL_ROWS) {
            return 0;
        }

        VitalSign latest = existingVitals.get(0);
        latest.setPatientId(String.valueOf(patientId));
        if (latest.getHeightCm() <= 0d) {
            latest.setHeightCm(spec.heightCm);
        }
        if (latest.getWeightKg() <= 0d) {
            latest.setWeightKg(spec.weightKg);
        }
        if (latest.getTimestamp() == null || latest.getTimestamp().trim().isEmpty()) {
            latest.setTimestamp(DateTimeUtils.minutesAgo(spec.latestMinutesAgo));
        }

        int inserted = 0;
        List<VitalSign> olderVitals = DemoHistorySeeder.generatePastVitals(
                String.valueOf(patientId),
                latest,
                spec.riskLevel,
                TARGET_HISTORY_RECORDS
        );
        for (VitalSign vital : olderVitals) {
            if (vitalDao.insertVital(vital) > 0L) {
                inserted++;
            }
        }
        return inserted;
    }

    private static int ensurePredictionExists(PredictionDao predictionDao, int patientId, DemoPatientSpec spec) {
        if (predictionDao.getLatestPredictionByPatientId(patientId) != null) {
            return 0;
        }
        return predictionDao.insertPrediction(spec.toPrediction(String.valueOf(patientId))) > 0L ? 1 : 0;
    }

    private static int ensureAlertExists(AlertDao alertDao, int patientId, DemoPatientSpec spec) {
        AlertItem alertItem = spec.toAlert(String.valueOf(patientId));
        if (alertItem == null) {
            return 0;
        }

        List<AlertItem> patientAlerts = alertDao.getAlertsByPatientId(patientId);
        for (AlertItem existingAlert : patientAlerts) {
            if (equalsIgnoreCase(spec.alertType, existingAlert.getType())
                    && equalsIgnoreCase(spec.riskLevel, existingAlert.getSeverity())) {
                return 0;
            }
        }
        return alertDao.insertAlert(alertItem) > 0L ? 1 : 0;
    }

    private static Patient findDemoPatient(List<Patient> allPatients, String name, String bedNumber) {
        for (Patient patient : allPatients) {
            if (equalsIgnoreCase(name, patient.getName())
                    && equalsIgnoreCase(bedNumber, patient.getBedNumber())) {
                return patient;
            }
        }
        return null;
    }

    private static boolean equalsIgnoreCase(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.trim().equalsIgnoreCase(b.trim());
    }

    private static int parseId(String rawId) {
        if (rawId == null || rawId.trim().isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(rawId.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private static List<DemoPatientSpec> getDemoSpecs() {
        List<DemoPatientSpec> specs = new ArrayList<>();

        specs.add(new DemoPatientSpec(
                "Sarah Johnson",
                64,
                "Female",
                "ICU-04",
                "ICU-04",
                Constants.RISK_CRITICAL,
                132,
                86,
                85,
                55,
                30,
                38.6,
                164.0,
                62.0,
                3,
                180,
                91.0,
                "Critical deterioration risk due to low SpO2, hypotension, and tachycardia.",
                "Critical Deterioration Alert",
                "Prediction indicates critical deterioration risk requiring immediate attention.",
                "86",
                "%"
        ));

        specs.add(new DemoPatientSpec(
                "Michael Ross",
                57,
                "Male",
                "ICU-01",
                "ICU-01",
                Constants.RISK_WARNING,
                121,
                92,
                98,
                64,
                24,
                37.8,
                172.0,
                78.0,
                5,
                165,
                74.0,
                "Warning trend detected with elevated heart rate and rising respiratory effort.",
                "Deterioration Warning",
                "Prediction indicates warning-level deterioration risk. Continue close monitoring.",
                "121",
                "BPM"
        ));

        specs.add(new DemoPatientSpec(
                "Priya Nair",
                46,
                "Female",
                "ICU-07",
                "ICU-07",
                Constants.RISK_STABLE,
                84,
                98,
                118,
                76,
                17,
                36.8,
                160.0,
                58.0,
                4,
                150,
                28.0,
                "Stable trajectory with no immediate deterioration signal.",
                null,
                null,
                null,
                null
        ));

        specs.add(new DemoPatientSpec(
                "Ayaan Mehta",
                69,
                "Male",
                "ICU-09",
                "ICU-09",
                Constants.RISK_WARNING,
                114,
                90,
                94,
                60,
                23,
                38.1,
                176.0,
                82.0,
                2,
                132,
                69.0,
                "Warning-level deterioration risk with borderline oxygenation and fever trend.",
                "Prediction Alert",
                "Prediction indicates warning-level deterioration risk. Reassess soon.",
                "69",
                "score"
        ));

        return specs;
    }

    public static final class DemoLoadResult {
        private final int insertedPatients;
        private final int insertedVitals;
        private final int insertedPredictions;
        private final int insertedAlerts;
        private final boolean skippedAsAlreadyLoaded;

        DemoLoadResult(
                int insertedPatients,
                int insertedVitals,
                int insertedPredictions,
                int insertedAlerts,
                boolean skippedAsAlreadyLoaded
        ) {
            this.insertedPatients = insertedPatients;
            this.insertedVitals = insertedVitals;
            this.insertedPredictions = insertedPredictions;
            this.insertedAlerts = insertedAlerts;
            this.skippedAsAlreadyLoaded = skippedAsAlreadyLoaded;
        }

        public int getInsertedPatients() {
            return insertedPatients;
        }

        public int getInsertedVitals() {
            return insertedVitals;
        }

        public int getInsertedPredictions() {
            return insertedPredictions;
        }

        public int getInsertedAlerts() {
            return insertedAlerts;
        }

        public boolean isSkippedAsAlreadyLoaded() {
            return skippedAsAlreadyLoaded;
        }

        public boolean hasAnyInsertions() {
            return insertedPatients > 0
                    || insertedVitals > 0
                    || insertedPredictions > 0
                    || insertedAlerts > 0;
        }
    }

    private static final class DemoPatientSpec {
        private final String name;
        private final int age;
        private final String sex;
        private final String ward;
        private final String bedNumber;
        private final String riskLevel;
        private final int heartRate;
        private final int spo2;
        private final int systolicBp;
        private final int diastolicBp;
        private final int respiratoryRate;
        private final double temperature;
        private final double heightCm;
        private final double weightKg;
        private final int latestMinutesAgo;
        private final int createdMinutesAgo;
        private final double predictionScore;
        private final String predictionSummary;
        private final String alertType;
        private final String alertMessage;
        private final String alertValue;
        private final String alertUnit;

        DemoPatientSpec(
                String name,
                int age,
                String sex,
                String ward,
                String bedNumber,
                String riskLevel,
                int heartRate,
                int spo2,
                int systolicBp,
                int diastolicBp,
                int respiratoryRate,
                double temperature,
                double heightCm,
                double weightKg,
                int latestMinutesAgo,
                int createdMinutesAgo,
                double predictionScore,
                String predictionSummary,
                String alertType,
                String alertMessage,
                String alertValue,
                String alertUnit
        ) {
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.ward = ward;
            this.bedNumber = bedNumber;
            this.riskLevel = riskLevel;
            this.heartRate = heartRate;
            this.spo2 = spo2;
            this.systolicBp = systolicBp;
            this.diastolicBp = diastolicBp;
            this.respiratoryRate = respiratoryRate;
            this.temperature = temperature;
            this.heightCm = heightCm;
            this.weightKg = weightKg;
            this.latestMinutesAgo = latestMinutesAgo;
            this.createdMinutesAgo = createdMinutesAgo;
            this.predictionScore = predictionScore;
            this.predictionSummary = predictionSummary;
            this.alertType = alertType;
            this.alertMessage = alertMessage;
            this.alertValue = alertValue;
            this.alertUnit = alertUnit;
        }

        private Patient toPatient() {
            return new Patient(
                    null,
                    name,
                    age,
                    sex,
                    ward,
                    bedNumber,
                    riskLevel,
                    DateTimeUtils.minutesAgo(createdMinutesAgo)
            );
        }

        private VitalSign toCurrentVital(String patientId) {
            return new VitalSign(
                    null,
                    patientId,
                    heartRate,
                    spo2,
                    systolicBp,
                    diastolicBp,
                    respiratoryRate,
                    temperature,
                    heightCm,
                    weightKg,
                    DateTimeUtils.minutesAgo(latestMinutesAgo)
            );
        }

        private Prediction toPrediction(String patientId) {
            return new Prediction(
                    null,
                    patientId,
                    riskLevel,
                    predictionScore,
                    predictionSummary,
                    DateTimeUtils.minutesAgo(Math.max(1, latestMinutesAgo))
            );
        }

        private AlertItem toAlert(String patientId) {
            if (alertType == null || alertMessage == null) {
                return null;
            }
            return new AlertItem(
                    null,
                    patientId,
                    alertType,
                    riskLevel,
                    alertMessage,
                    DateTimeUtils.minutesAgo(Math.max(1, latestMinutesAgo)),
                    alertValue == null ? String.format(Locale.US, "%.0f", predictionScore) : alertValue,
                    alertUnit == null ? "score" : alertUnit,
                    (int) Math.round(predictionScore),
                    false
            );
        }
    }
}
