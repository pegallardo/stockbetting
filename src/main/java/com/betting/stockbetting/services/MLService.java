package com.betting.stockbetting.services;

import com.betting.stockbetting.models.PredictionResponse;

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
    PredictionResponse predict(String symbol, double open, double high, double low, double close, long volume);
}