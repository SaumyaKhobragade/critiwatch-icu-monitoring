package com.example.critiwatch.database;

import android.content.Context;

import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.Prediction;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.List;

public final class DatabaseSeeder {

    private DatabaseSeeder() {
    }

    public static void seedIfEmpty(Context context) {
        PatientDao patientDao = new PatientDao(context);
        List<Patient> existing = patientDao.getAllPatients();
        if (!existing.isEmpty()) {
            return;
        }

        VitalDao vitalDao = new VitalDao(context);
        PredictionDao predictionDao = new PredictionDao(context);
        AlertDao alertDao = new AlertDao(context);

        long sarahId = patientDao.insertPatient(new Patient(
                null,
                "Sarah Johnson",
                64,
                "Female",
                "ICU-A",
                "ICU-04",
                Constants.RISK_CRITICAL,
                DateTimeUtils.minutesAgo(140)
        ));

        long michaelId = patientDao.insertPatient(new Patient(
                null,
                "Michael Ross",
                57,
                "Male",
                "ICU-A",
                "ICU-01",
                Constants.RISK_WARNING,
                DateTimeUtils.minutesAgo(90)
        ));

        long priyaId = patientDao.insertPatient(new Patient(
                null,
                "Priya Nair",
                46,
                "Female",
                "ICU-B",
                "ICU-07",
                Constants.RISK_STABLE,
                DateTimeUtils.minutesAgo(45)
        ));

        insertVitals(vitalDao, sarahId,
                new VitalSign(null, String.valueOf(sarahId), 132, 86, 85, 55, 30, 38.6, 164, 62, DateTimeUtils.minutesAgo(2)),
                new VitalSign(null, String.valueOf(sarahId), 128, 88, 88, 58, 28, 38.4, 164, 62, DateTimeUtils.minutesAgo(8)));

        insertVitals(vitalDao, michaelId,
                new VitalSign(null, String.valueOf(michaelId), 112, 92, 98, 64, 24, 37.7, 172, 78, DateTimeUtils.minutesAgo(4)),
                new VitalSign(null, String.valueOf(michaelId), 108, 93, 100, 66, 22, 37.5, 172, 78, DateTimeUtils.minutesAgo(12)));

        insertVitals(vitalDao, priyaId,
                new VitalSign(null, String.valueOf(priyaId), 84, 98, 118, 76, 17, 36.8, 160, 58, DateTimeUtils.minutesAgo(3)),
                new VitalSign(null, String.valueOf(priyaId), 82, 98, 120, 78, 16, 36.7, 160, 58, DateTimeUtils.minutesAgo(11)));

        predictionDao.insertPrediction(new Prediction(
                null,
                String.valueOf(sarahId),
                Constants.RISK_CRITICAL,
                91.0,
                "High probability of acute deterioration within 4-6 hours.",
                DateTimeUtils.minutesAgo(2)
        ));
        predictionDao.insertPrediction(new Prediction(
                null,
                String.valueOf(michaelId),
                Constants.RISK_WARNING,
                77.0,
                "Moderate risk trend detected. Continue close monitoring.",
                DateTimeUtils.minutesAgo(4)
        ));
        predictionDao.insertPrediction(new Prediction(
                null,
                String.valueOf(priyaId),
                Constants.RISK_STABLE,
                28.0,
                "Stable trajectory with no immediate deterioration signal.",
                DateTimeUtils.minutesAgo(3)
        ));

        alertDao.insertAlert(new AlertItem(
                null,
                String.valueOf(sarahId),
                "Low SpO2",
                Constants.RISK_CRITICAL,
                "SpO2 dropped below 88% for over 4 minutes.",
                DateTimeUtils.minutesAgo(2),
                "86",
                "%",
                92,
                false
        ));
        alertDao.insertAlert(new AlertItem(
                null,
                String.valueOf(michaelId),
                "High Heart Rate",
                Constants.RISK_WARNING,
                "Sustained tachycardia detected over the last 15 minutes.",
                DateTimeUtils.minutesAgo(5),
                "124",
                "BPM",
                78,
                false
        ));
    }

    private static void insertVitals(VitalDao vitalDao, long patientId, VitalSign first, VitalSign second) {
        if (patientId <= 0) {
            return;
        }
        first.setPatientId(String.valueOf(patientId));
        second.setPatientId(String.valueOf(patientId));
        vitalDao.insertVital(first);
        vitalDao.insertVital(second);
    }
}
