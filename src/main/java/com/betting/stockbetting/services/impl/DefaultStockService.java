package com.betting.stockbetting.services.impl;

import com.betting.stockbetting.services.StockService;
import com.betting.stockbetting.exceptions.StockNotFoundException;
import com.betting.stockbetting.models.StockData;
import com.betting.stockbetting.repositories.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the StockService interface.
 */
@Service
public class DefaultStockService implements StockService {

    private final StockRepository stockRepository;

    public DefaultStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<StockData> getStockData(String symbol) {
        List<StockData> stockData = stockRepository.findBySymbol(symbol);
        if (stockData.isEmpty()) {
            throw new StockNotFoundException(symbol);
        }
        return stockData;
    }

    @Override
    public StockData saveStockData(StockData stockData) {
        
        return stockRepository.save(stockData);
    }
}