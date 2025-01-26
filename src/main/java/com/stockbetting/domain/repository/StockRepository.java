package com.stockbetting.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockbetting.domain.entity.StockData;

import java.util.List;

public interface StockRepository extends JpaRepository<StockData, Long> {
    List<StockData> findBySymbol(String symbol);
}
