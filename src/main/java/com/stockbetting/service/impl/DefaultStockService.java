package com.stockbetting.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

import com.stockbetting.domain.entity.StockData;
import com.stockbetting.domain.repository.StockRepository;
import com.stockbetting.service.StockService;

/**
 * Default implementation of the StockService interface.
 * Handles stock data operations using async processing for better performance.
 */
@Service
public class DefaultStockService implements StockService {

    /**
     * Repository for stock data persistence operations.
     */
    private final StockRepository stockRepository;

    /**
     * ExecutorService for handling async operations.
     */
    private final ExecutorService executorService;

    /**
     * Constructor for DefaultStockService.
     *
     * @param stockRepository Repository for stock data operations
     */
    public DefaultStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );
    }

    /**
     * Retrieves stock data for a given symbol asynchronously.
     *
     * @param symbol The stock symbol to retrieve data for
     * @return CompletableFuture of a list of StockData for the specified symbol
     */
    @Override
    public CompletableFuture<List<StockData>> getStockDataAsync(String symbol) {
        return CompletableFuture.supplyAsync(() -> 
            stockRepository.findBySymbol(symbol),
            executorService
        );
    }

    /**
     * Saves new stock data asynchronously.
     *
     * @param stockData The stock data to save
     * @return CompletableFuture of the saved StockData
     */
    @Override
    public CompletableFuture<StockData> saveStockDataAsync(StockData stockData) {
        return CompletableFuture.supplyAsync(() ->
            stockRepository.save(stockData),
            executorService
        );
    }

    /**
     * Cleanup method to properly shutdown the executor service.
     * Called when the Spring container is destroying the bean.
     */
    @PreDestroy
    public void cleanup() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
