package com.stockbetting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a detailed stock prediction response.
 * 
 * @param trend The predicted market trend (UP/DOWN/STABLE)
 * @param confidence Confidence level of the prediction
 * @param message Additional prediction details or insights
 * @param recommendation Trading recommendation based on prediction
 */

@Data
@AllArgsConstructor
public class PredictionResponse {
    private String symbol;
    private String date;
    private double predictedOdds;
    private String message;
}
