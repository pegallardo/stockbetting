package com.morpheus.stockbetting.dto.response;

/**
 * Data Transfer Object representing a stock market prediction response.
 * Contains detailed information about market predictions including direction,
 * confidence levels, and trading recommendations.
 */
public class PredictionResponse {
    
    /** Market movement direction (UP/DOWN/STABLE) */
    private String direction;
    
    /** Trading recommendation based on analysis */
    private String recommendation;
    
    /** Confidence score of the prediction (0.0 to 1.0) */
    private double confidence;
    
    /** Detailed explanation of the prediction */
    private String explanation;
    
    /** Legacy field for predicted market trend */
    private String predictedTrend;

    /**
     * Creates a detailed prediction response with all parameters.
     * 
     * @param direction Market movement direction
     * @param recommendation Trading action recommendation
     * @param confidence Prediction confidence score
     * @param explanation Detailed prediction explanation
     */
    public PredictionResponse(String direction, String recommendation, double confidence, String explanation) {
        this.direction = direction;
        this.recommendation = recommendation;
        this.confidence = confidence;
        this.explanation = explanation;
    }

    /**
     * Creates a simple prediction response with trend only.
     * 
     * @param predictedTrend Market trend prediction
     */
    public PredictionResponse(String predictedTrend) {
        this.predictedTrend = predictedTrend;
    }

    // Getters and Setters
    public String getPredictedTrend() {
        return predictedTrend;
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