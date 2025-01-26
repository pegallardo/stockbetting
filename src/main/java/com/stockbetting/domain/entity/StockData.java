package com.stockbetting.domain.entity;

import jakarta.persistence.*;

/**
 * Entity representing stock market data for a specific stock symbol.
 * This is mapped to the database table "stock_data".
 */
@Entity
@Table(name = "stock_data")
public class StockData {

    /**
     * Primary key for the stock_data table.
     * Auto-generated value using IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Stock symbol (e.g., "AAPL" for Apple).
     * Cannot be null.
     */
    @Column(nullable = false, length = 10) // Restricting symbol length for common standards.
    private String symbol;

    /**
     * Opening price of the stock.
     * Cannot be null.
     */
    @Column(nullable = false)
    private double open;

    /**
     * Highest price of the stock during the trading session.
     * Cannot be null.
     */
    @Column(nullable = false)
    private double high;

    /**
     * Lowest price of the stock during the trading session.
     * Cannot be null.
     */
    @Column(nullable = false)
    private double low;

    /**
     * Closing price of the stock.
     * Cannot be null.
     */
    @Column(nullable = false)
    private double close;

    /**
     * Total trading volume of the stock.
     * Cannot be null.
     */
    @Column(nullable = false)
    private long volume;

    /**
     * Date of the stock data in YYYY-MM-DD format.
     * Cannot be null.
     */
    @Column(nullable = false, length = 10) // Enforcing the date format length.
    private String date;

    public String getSymbol() { return symbol; }
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public long getVolume() { return volume; }
}