package com.stockbetting.service;

import java.util.concurrent.CompletableFuture;

import com.stockbetting.dto.response.PredictionResponse;

public interface MLService {
    /**
     * Predict stock trend using machine learning.
     *
     * @param symbol    Stock symbol.
     * @param open      Opening price.
     * @param high      High price.
     * @param low       Low price.
     * @param close     Closing price.
     * @param volume    Volume of stocks traded.
     * @return Prediction response with trend and odds.
     */
    CompletableFuture<PredictionResponse> predict(String symbol, double open, double high, 
                                                double low, double close, long volume);
}