package com.stocks.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricalPrice(
        Long id,
        Long stockId,
        LocalDate priceDate,
        BigDecimal openPrice,
        BigDecimal closePrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        long volume
) {}
