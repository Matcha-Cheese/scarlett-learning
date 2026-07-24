package com.stocks.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddPriceRequest(

        @NotNull(message = "Price date is required")
        LocalDate priceDate,

        @NotNull @DecimalMin(value = "0.0001", message = "Open price must be positive")
        BigDecimal openPrice,

        @NotNull @DecimalMin(value = "0.0001", message = "Close price must be positive")
        BigDecimal closePrice,

        @NotNull @DecimalMin(value = "0.0001", message = "High price must be positive")
        BigDecimal highPrice,

        @NotNull @DecimalMin(value = "0.0001", message = "Low price must be positive")
        BigDecimal lowPrice,

        @Min(value = 0, message = "Volume cannot be negative")
        long volume
) {}
