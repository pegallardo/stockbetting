package com.morpheus.stockbetting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.morpheus.stockbetting.domain.entity.StockData;
import com.morpheus.stockbetting.dto.response.PredictionResponse;
import com.morpheus.stockbetting.service.MLService;
import com.morpheus.stockbetting.service.StockService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for managing stock data and predictions.
 * This controller provides endpoints to retrieve, save, and predict stock data trends.
 */
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService; // Service for managing stock data operations (retrieving and saving stock data)
    private final MLService mlService; // Service for machine learning predictions related to stock trends

    public StockController(StockService stockService, MLService mlService) {
        this.stockService = stockService;
        this.mlService = mlService;
    }

    /**
     * Endpoint to retrieve all stock data for a given stock symbol.
     *
     * @param symbol The stock symbol (e.g., "AAPL").
     * @return A CompletableFuture of ResponseEntity containing a list of StockData objects for the given symbol.
     */
    @GetMapping("/{symbol}")
    public CompletableFuture<ResponseEntity<List<StockData>>> getStockData(@PathVariable String symbol) {
        return stockService.getStockDataAsync(symbol)
            .thenApply(ResponseEntity::ok);
    }

    /**
     * Endpoint to predict the stock trend based on provided stock data.
     *
     * @param stockData The StockData object containing the relevant stock information for prediction.
     * @return A CompletableFuture of ResponseEntity containing a PredictionResponse object with the predicted trend.
     */
    @PostMapping("/predict")
    public CompletableFuture<ResponseEntity<PredictionResponse>> predictStockTrend(@RequestBody StockData stockData) {
        return mlService.predict(
                stockData.getSymbol(),
                stockData.getOpen(),
                stockData.getHigh(),
                stockData.getLow(),
                stockData.getClose(),
                stockData.getVolume()
        ).thenApply(ResponseEntity::ok);
    }

    /**
     * Endpoint to save a new stock data entry to the database.
     *
     * @param stockData The StockData object to save in the database.
     * @return A CompletableFuture of ResponseEntity containing the saved StockData object.
     */
    @PostMapping
    public CompletableFuture<ResponseEntity<StockData>> saveStockData(@RequestBody StockData stockData) {
        return stockService.saveStockDataAsync(stockData)
            .thenApply(ResponseEntity::ok);
    }
}
