package com.stockbetting.service;

import java.util.List;

import com.stockbetting.domain.entity.StockData;

public interface StockService {
    /**
     * Fetch stock data by symbol.
     *
     * @param symbol Stock symbol.
     * @return List of stock data.
     */
    List<StockData> getStockData(String symbol);

    /**
     * Save stock data to the database.
     *
     * @param stockData Stock data to save.
     * @return Saved stock data.
     */
    StockData saveStockData(StockData stockData);
}
