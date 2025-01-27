package com.morpheus.stockbetting.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.morpheus.stockbetting.dto.response.PredictionResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Unit tests for DefaultMLService implementation using BDD style testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultMLService Behavior")
public class DefaultMLServiceTest {

    @InjectMocks
    private DefaultMLService mlService;

    private String testSymbol;
    private double testOpen;
    private double testHigh;
    private double testLow;
    private double testClose;
    private long testVolume;

    @BeforeEach
    void setUp() {
        // Initialize test data with realistic market values
        testSymbol = "AAPL";
        testOpen = 150.0;
        testHigh = 155.0;
        testLow = 148.0;
        testClose = 152.0;
        testVolume = 1000000L;
    }

    @Test
    @DisplayName("When valid market data provided, then return complete prediction")
    void whenValidMarketData_thenReturnCompletePrediction() {
        // Act
        CompletableFuture<PredictionResponse> result = mlService.predict(
            testSymbol, testOpen, testHigh, testLow, testClose, testVolume
        );

        // Assert
        PredictionResponse prediction = result.join();
        assertNotNull(prediction.getDirection());
        assertNotNull(prediction.getRecommendation());
        assertTrue(prediction.getConfidence() >= 0.0 && prediction.getConfidence() <= 1.0);
        assertNotNull(prediction.getExplanation());
    }

    @Test
    @DisplayName("When closing price is higher, then predict upward trend")
    void whenClosingPriceHigher_thenPredictUpwardTrend() {
        // Arrange
        double highClose = 160.0;

        // Act
        CompletableFuture<PredictionResponse> result = mlService.predict(
            testSymbol, testOpen, testHigh, testLow, highClose, testVolume
        );

        // Assert
        PredictionResponse prediction = result.join();
        assertEquals("UP", prediction.getDirection());
        assertEquals("BUY", prediction.getRecommendation());
    }

    @Test
    @DisplayName("When closing price is lower, then predict downward trend")
    void whenClosingPriceLower_thenPredictDownwardTrend() {
        // Arrange
        double lowClose = 140.0;

        // Act
        CompletableFuture<PredictionResponse> result = mlService.predict(
            testSymbol, testOpen, testHigh, testLow, lowClose, testVolume
        );

        // Assert
        PredictionResponse prediction = result.join();
        assertEquals("DOWN", prediction.getDirection());
        assertEquals("SELL", prediction.getRecommendation());
    }

    @Test
    @DisplayName("When volume is negative, then throw IllegalArgumentException")
    void whenVolumeIsNegative_thenThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            mlService.predict(testSymbol, testOpen, testHigh, testLow, testClose, -1L)
        );
    }

    @Test
    @DisplayName("When symbol is null, then throw IllegalArgumentException")
    void whenSymbolIsNull_thenThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            mlService.predict(null, testOpen, testHigh, testLow, testClose, testVolume)
        );
    }

    @Test
    @DisplayName("When strong market signals present, then predict with high confidence")
    void whenStrongMarketSignals_thenPredictWithHighConfidence() {
        // Arrange
        double significantGap = 10.0;
        double strongClose = testOpen + significantGap;

        // Act
        CompletableFuture<PredictionResponse> result = mlService.predict(
            testSymbol, testOpen, testHigh, testLow, strongClose, testVolume * 2
        );

        // Assert
        PredictionResponse prediction = result.join();
        assertTrue(prediction.getConfidence() > 0.8);
        assertNotNull(prediction.getExplanation());
    }
}