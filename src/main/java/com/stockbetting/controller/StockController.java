package com.stockbetting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stockbetting.domain.entity.StockData;
import com.stockbetting.dto.response.PredictionResponse;
import com.stockbetting.service.MLService;
import com.stockbetting.service.StockService;

import java.util.List;

/**
 * REST Controller for managing stock data and predictions.
 * This controller provides endpoints to retrieve, save, and predict stock data trends.
 */
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor  // Lombok annotation to automatically generate a constructor with required fields (stockService and mlService)
public class StockController {

    private final StockService stockService; // Service for managing stock data operations (retrieving and saving stock data)
    private final MLService mlService; // Service for machine learning predictions related to stock trends

    /**
     * Endpoint to retrieve all stock data for a given stock symbol.
     * 
     * @param symbol The stock symbol (e.g., "AAPL").
     * @return A ResponseEntity containing a list of StockData objects for the given symbol.
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<List<StockData>> getStockData(@PathVariable String symbol) {
        // Retrieve stock data from the stockService based on the symbol
        List<StockData> stockData = stockService.getStockData(symbol);
        // Return the retrieved stock data in the response body with HTTP status 200 OK
        return ResponseEntity.ok(stockData);
    }

    /**
     * Endpoint to predict the stock trend based on provided stock data.
     * 
     * @param stockData The StockData object containing the relevant stock information for prediction.
     * @return A ResponseEntity containing a PredictionResponse object with the predicted trend.
     */
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> predictStockTrend(@RequestBody StockData stockData) {
        // Pass the stock data to the mlService to predict the trend for the given stock
        PredictionResponse response = mlService.predict(
                stockData.getSymbol(),
                stockData.getOpen(),
                stockData.getHigh(),
                stockData.getLow(),
                stockData.getClose(),
                stockData.getVolume()
        );
        // Return the prediction response in the response body with HTTP status 200 OK
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to save a new stock data entry to the database.
     * 
     * @param stockData The StockData object to save in the database.
     * @return A ResponseEntity containing the saved StockData object with HTTP status 200 OK.
     */
    @PostMapping
    public ResponseEntity<StockData> saveStockData(@RequestBody StockData stockData) {
        // Save the stock data using the stockService
        StockData savedData = stockService.saveStockData(stockData);
        // Return the saved stock data in the response body with HTTP status 200 OK
        return ResponseEntity.ok(savedData);
    }
}
