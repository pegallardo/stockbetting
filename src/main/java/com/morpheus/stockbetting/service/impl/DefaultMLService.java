package com.morpheus.stockbetting.service.impl;

import java.util.concurrent.*;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vectors;
import org.springframework.stereotype.Service;

import com.morpheus.stockbetting.dto.response.PredictionResponse;
import com.morpheus.stockbetting.service.MLService;

import jakarta.annotation.PreDestroy;

/**
 * Implementation of the {@link MLService} interface using a Logistic Regression model.
 * This service predicts the stock price trend (upward or downward) based on historical data.
 */
@Service
public class DefaultMLService implements MLService {

    /**
     * The pre-trained Logistic Regression model used for prediction.
     */
    private final LogisticRegressionModel logisticRegressionModel;

    /**
     * ExecutorService for handling async operations.
     */
    private final ExecutorService executorService;

    /**
     * Constructor for DefaultMLService.
     *
     * @param logisticRegressionModel the pre-trained Logistic Regression model
     */
    public DefaultMLService(LogisticRegressionModel logisticRegressionModel) {
        this.logisticRegressionModel = logisticRegressionModel;
        this.executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );
    }

    /**
     * Predicts the stock price trend for a given symbol and its historical data.
     *
     * @param symbol the stock symbol
     * @param open the opening price
     * @param high the highest price during the period
     * @param low the lowest price during the period
     * @param close the closing price
     * @param volume the trading volume
     * @return a CompletableFuture of {@link PredictionResponse} containing the prediction and probability
     */
    @Override
    public CompletableFuture<PredictionResponse> predict(String symbol, double open, double high, double low, double close, long volume) {
        
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol cannot be null");
        }
        if (volume < 0) {
            throw new IllegalArgumentException("Volume cannot be negative");
        }

        return CompletableFuture.supplyAsync(() -> {
            // Create the feature vector from the input data
            double[] features = {open, high, low, close, volume};

            // Predict the class (1 for upward trend, 0 for downward trend)
            double prediction = logisticRegressionModel.predict(Vectors.dense(features));

            // Predict probabilities for both classes (upward and downward)
            double[] probabilities = logisticRegressionModel.predictProbability(Vectors.dense(features)).toArray();

            // Generate a message based on the prediction
            String message = prediction == 1.0 ? "Price likely to go up" : "Price likely to go down";

            // Return the prediction response
            return new PredictionResponse(symbol, "2025-01-23", probabilities[1], message);
        }, executorService);
    }

    /**
     * Cleanup method to properly shutdown the executor service.
     * Called when the Spring container is destroying the bean.
     */
    @PreDestroy
    public void cleanup() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
