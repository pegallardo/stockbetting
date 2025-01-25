package com.betting.stockbetting.repositories;

import com.betting.stockbetting.models.StockData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<StockData, Long> {
    List<StockData> findBySymbol(String symbol);
}
