package com.betting.stockbetting.services.impl;

import com.betting.stockbetting.models.*;
import com.betting.stockbetting.services.MLService;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vectors;
import org.springframework.stereotype.Service;

@Service
public class DefaultMLService implements MLService {

    private final LogisticRegressionModel logisticRegressionModel;

    public DefaultMLService(LogisticRegressionModel logisticRegressionModel) {
        this.logisticRegressionModel = logisticRegressionModel;
    }

    @Override
    public PredictionResponse predict(String symbol, double open, double high, double low, double close, long volume) {
        double[] features = {open, high, low, close, volume};

        // Predict the class (1 for upward trend, 0 for downward trend)
        double prediction = logisticRegressionModel.predict(Vectors.dense(features));

        // Predict probabilities
        double[] probabilities = logisticRegressionModel.predictProbability(Vectors.dense(features)).toArray();

        String message = prediction == 1.0 ? "Price likely to go up" : "Price likely to go down";

        return new PredictionResponse(symbol, "2025-01-23", probabilities[1], message);
    }
}