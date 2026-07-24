package com.stocks.dto;

import com.stocks.model.Stock;

public record StockResponse(Long id, String symbol, String companyName, String sector, String exchange) {
    public static StockResponse fromDomain(Stock stock) {
        return new StockResponse(stock.id(), stock.symbol(), stock.companyName(), stock.sector(), stock.exchange());
    }
}
