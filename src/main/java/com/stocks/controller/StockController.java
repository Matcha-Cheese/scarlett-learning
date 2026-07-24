package com.stocks.controller;

import com.stocks.dto.CreateStockRequest;
import com.stocks.dto.StockResponse;
import com.stocks.model.Stock;
import com.stocks.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockResponse> getAllStocks() {
        return stockService.getAllStocks().stream().map(StockResponse::fromDomain).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long id) {
        return stockService.getStockById(id)
                .map(StockResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockResponse> createStock(@Valid @RequestBody CreateStockRequest request) {
        Stock stock = new Stock(null, request.symbol(), request.companyName(), request.sector(), request.exchange());
        Stock saved = stockService.addStock(stock);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.id()).toUri();
        return ResponseEntity.created(location).body(StockResponse.fromDomain(saved));
    }
}
