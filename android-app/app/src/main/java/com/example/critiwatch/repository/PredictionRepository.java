package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.PredictionDao;
import com.example.critiwatch.models.Prediction;

import java.util.List;

public class PredictionRepository {

    private final PredictionDao predictionDao;

    public PredictionRepository(Context context) {
        this.predictionDao = new PredictionDao(context);
    }

    public long addPrediction(Prediction prediction) {
        return predictionDao.insertPrediction(prediction);
    }

    public List<Prediction> getPredictionsByPatientId(int patientId) {
        return predictionDao.getPredictionsByPatientId(patientId);
    }

    public Prediction getLatestPredictionByPatientId(int patientId) {
        return predictionDao.getLatestPredictionByPatientId(patientId);
    }
}
