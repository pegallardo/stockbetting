package com.stockbetting.dto.response;

/**
 * Represents a detailed stock prediction response.
 * 
 * @param trend The predicted market trend (UP/DOWN/STABLE)
 * @param confidence Confidence level of the prediction
 * @param message Additional prediction details or insights
 * @param recommendation Trading recommendation based on prediction
 */

 public class PredictionResponse {
    private String direction;
    private String recommendation;
    private double confidence;
    private String explanation;

    public PredictionResponse(String direction, String recommendation, double confidence, String explanation) {
        this.direction = direction;
        this.recommendation = recommendation;
        this.confidence = confidence;
        this.explanation = explanation;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
