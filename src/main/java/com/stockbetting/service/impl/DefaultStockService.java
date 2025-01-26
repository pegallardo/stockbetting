package com.stockbetting.service.impl;

import org.springframework.stereotype.Service;

import com.stockbetting.domain.entity.StockData;
import com.stockbetting.domain.repository.StockRepository;
import com.stockbetting.exception.StockNotFoundException;
import com.stockbetting.service.StockService;

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