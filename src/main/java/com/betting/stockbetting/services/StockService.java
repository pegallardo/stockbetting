package com.betting.stockbetting.services;

import com.betting.stockbetting.models.StockData;

import java.util.List;

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
