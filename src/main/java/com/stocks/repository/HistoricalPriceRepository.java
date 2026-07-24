package com.stocks.repository;

import com.stocks.model.HistoricalPrice;
import java.util.List;

public interface HistoricalPriceRepository {
    List<HistoricalPrice> findByStockId(Long stockId);
    HistoricalPrice save(HistoricalPrice price);
}
