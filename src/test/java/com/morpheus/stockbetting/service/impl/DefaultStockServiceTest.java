package com.morpheus.stockbetting.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.morpheus.stockbetting.domain.entity.StockData;
import com.morpheus.stockbetting.domain.repository.StockRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Unit tests for DefaultStockService implementation using BDD style testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultStockService Behavior")
public class DefaultStockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private DefaultStockService stockService;

    private StockData testStockData;

    @BeforeEach
    void setUp() {
        testStockData = new StockData();
        testStockData.setSymbol("AAPL");
        testStockData.setOpen(150.0);
        testStockData.setHigh(155.0);
        testStockData.setLow(148.0);
        testStockData.setClose(152.0);
        testStockData.setVolume(1000000L);
    }

    @Test
    @DisplayName("When valid symbol provided, then return stock data")
    void whenValidSymbol_thenReturnStockData() {
        // Arrange
        String symbol = "AAPL";
        List<StockData> expectedData = Arrays.asList(testStockData);
        when(stockRepository.findBySymbol(symbol)).thenReturn(expectedData);

        // Act
        CompletableFuture<List<StockData>> result = stockService.getStockDataAsync(symbol);

        // Assert
        assertNotNull(result);
        List<StockData> actualData = result.join();
        assertEquals(expectedData.size(), actualData.size());
        assertEquals(expectedData.get(0).getSymbol(), actualData.get(0).getSymbol());
        verify(stockRepository).findBySymbol(symbol);
    }

    @Test
    @DisplayName("When valid stock data provided, then save successfully")
    void whenValidStockData_thenSaveSuccessfully() {
        // Arrange
        when(stockRepository.save(any(StockData.class))).thenReturn(testStockData);

        // Act
        CompletableFuture<StockData> result = stockService.saveStockDataAsync(testStockData);

        // Assert
        assertNotNull(result);
        StockData savedData = result.join();
        assertEquals(testStockData.getSymbol(), savedData.getSymbol());
        assertEquals(testStockData.getOpen(), savedData.getOpen());
        verify(stockRepository).save(testStockData);
    }

    @Test
    @DisplayName("When invalid symbol provided, then return empty result")
    void whenInvalidSymbol_thenReturnEmptyResult() {
        // Arrange
        String symbol = "INVALID";
        when(stockRepository.findBySymbol(symbol)).thenReturn(Arrays.asList());

        // Act
        CompletableFuture<List<StockData>> result = stockService.getStockDataAsync(symbol);

        // Assert
        assertNotNull(result);
        List<StockData> actualData = result.join();
        assertTrue(actualData.isEmpty());
        verify(stockRepository).findBySymbol(symbol);
    }

    @Test
    @DisplayName("When null stock data provided, then throw IllegalArgumentException")
    void whenNullStockData_thenThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            stockService.saveStockDataAsync(null));
        verify(stockRepository, never()).save(any());
    }
}