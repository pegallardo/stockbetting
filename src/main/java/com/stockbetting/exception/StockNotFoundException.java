
package com.stockbetting.exception;

/**
 * Exception thrown when requested stock data is not found
 */
public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String symbol) {
        super("Stock not found with symbol: " + symbol);
    }
}
