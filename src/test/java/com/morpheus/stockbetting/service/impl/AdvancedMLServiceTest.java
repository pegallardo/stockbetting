package com.morpheus.stockbetting.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import com.morpheus.stockbetting.dto.response.PredictionResponse;

import java.util.concurrent.CompletableFuture;

@DisplayName("AdvancedMLService Tests")
class AdvancedMLServiceTest {

    @Mock
    private SparkSession sparkSession;

    @Mock
    private CrossValidatorModel trainedModel;

    @InjectMocks
    private AdvancedMLService mlService;

    private MarketTestData marketData;

    private static class MarketTestData {
        String symbol = "AAPL";
        double open = 150.0;
        double high = 155.0;
        double low = 148.0;
        double close = 152.0;
        long volume = 1_000_000L;
    }

    @BeforeEach
    void setUp() {
        marketData = new MarketTestData();
        
        // Mocking DataFrameReader and Dataset<Row>
        DataFrameReader mockDataFrameReader = mock(DataFrameReader.class);
        Dataset<Row> mockDataset = mock(Dataset.class);
        
        // Mocking behavior for SparkSession
        when(sparkSession.read()).thenReturn(mockDataFrameReader);
        
        // Mocking behavior for DataFrameReader to return a Dataset<Row>
        when(mockDataFrameReader.format(anyString())).thenReturn(mockDataFrameReader);
        when(mockDataFrameReader.load()).thenReturn(mockDataset);
        
        // Mocking the trained model's transform method to return a Dataset<Row>
        when(trainedModel.transform(any(Dataset.class))).thenReturn(mockDataset);
        
        // Creating a PredictionResponse with the constructor
        PredictionResponse mockResponse = new PredictionResponse("UP", "BUY", 0.9, "Strong upward movement detected.");
        
        // Mocking CompletableFuture to return a predefined PredictionResponse
        when(mlService.predict(
            marketData.symbol,
            marketData.open,
            marketData.high,
            marketData.low,
            marketData.close,
            marketData.volume))
            .thenReturn(CompletableFuture.completedFuture(mockResponse));
    }

    @Nested
    @DisplayName("Given market trend predictions")
    class GivenMarketTrendPredictions {
        
        @Test
        @DisplayName("When strong upward movement, then predict buy with high confidence")
        void whenStrongUpwardMovement_thenPredictBuyWithHighConfidence() {
            double strongClose = marketData.open + 10.0;
            
            CompletableFuture<PredictionResponse> future = mlService.predict(
                marketData.symbol,
                marketData.open,
                marketData.high,
                marketData.low,
                strongClose,
                marketData.volume * 2
            );
            
            PredictionResponse prediction = future.join();
            assertEquals("UP", prediction.getDirection());
            assertEquals("BUY", prediction.getRecommendation());
            assertTrue(prediction.getConfidence() > 0.8);
        }

        @Test
        @DisplayName("When sharp downward movement, then predict sell with high volume")
        void whenSharpDownwardMovement_thenPredictSellWithHighVolume() {
            double weakClose = marketData.open - 8.0;
            
            CompletableFuture<PredictionResponse> future = mlService.predict(
                marketData.symbol,
                marketData.open,
                marketData.high,
                marketData.low,
                weakClose,
                marketData.volume * 3
            );
            
            PredictionResponse prediction = future.join();
            assertEquals("DOWN", prediction.getDirection());
            assertEquals("SELL", prediction.getRecommendation());
            assertTrue(prediction.getConfidence() > 0.7);
        }
    }

    @Nested
    @DisplayName("Given market volatility scenarios")
    class GivenMarketVolatilityScenarios {
        
        @ParameterizedTest
        @CsvSource({
            "160.0, 145.0, HIGH",
            "152.0, 148.0, LOW"
        })
        @DisplayName("When price range varies, then detect correct volatility level")
        void whenPriceRangeVaries_thenDetectCorrectVolatilityLevel(
            double high, double low, String expectedVolatility) {
            
            CompletableFuture<PredictionResponse> future = mlService.predict(
                marketData.symbol,
                marketData.open,
                high,
                low,
                marketData.close,
                marketData.volume
            );
            
            PredictionResponse prediction = future.join();
            assertTrue(prediction.getExplanation().contains(expectedVolatility + " volatility"));
        }
    }

    @Nested
    @DisplayName("Given invalid inputs")
    class GivenInvalidInputs {
        
        @Test
        @DisplayName("When volume is negative, then throw IllegalArgumentException")
        void whenVolumeIsNegative_thenThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () ->
                mlService.predict(
                    marketData.symbol,
                    marketData.open,
                    marketData.high,
                    marketData.low,
                    marketData.close,
                    -1L
                )
            );
        }

        @Test
        @DisplayName("When symbol is null, then throw IllegalArgumentException")
        void whenSymbolIsNull_thenThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () ->
                mlService.predict(
                    null,
                    marketData.open,
                    marketData.high,
                    marketData.low,
                    marketData.close,
                    marketData.volume
                )
            );
        }
    }

    @Test
    @DisplayName("When analyzing market data, then provide comprehensive analysis")
    void whenAnalyzingMarketData_thenProvideComprehensiveAnalysis() {
        CompletableFuture<PredictionResponse> future = mlService.predict(
            marketData.symbol,
            marketData.open,
            marketData.high,
            marketData.low,
            marketData.close,
            marketData.volume
        );
        
        PredictionResponse prediction = future.join();
        assertNotNull(prediction.getDirection());
        assertNotNull(prediction.getRecommendation());
        assertTrue(prediction.getConfidence() >= 0.0 && prediction.getConfidence() <= 1.0);
        assertTrue(prediction.getExplanation().length() > 20);
    }
}