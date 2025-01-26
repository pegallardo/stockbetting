package com.stockbetting.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.stockbetting.domain.entity.StockData;

/**
 * Service interface for managing stock data operations.
 * Provides methods for retrieving and saving stock data asynchronously.
 */
public interface StockService {
    
    /**
     * Retrieves stock data for a given symbol asynchronously.
     *
     * @param symbol The stock symbol to retrieve data for
     * @return CompletableFuture of a list of StockData for the specified symbol
     */
    CompletableFuture<List<StockData>> getStockDataAsync(String symbol);

    /**
     * Saves new stock data asynchronously.
     *
     * @param stockData The stock data to save
     * @return CompletableFuture of the saved StockData
     */
    CompletableFuture<StockData> saveStockDataAsync(StockData stockData);
}