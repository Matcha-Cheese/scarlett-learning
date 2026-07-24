package com.stocks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateStockRequest(

        @NotBlank(message = "Symbol is required")
        @Pattern(regexp = "[A-Z]{1,10}", message = "Symbol must be 1-10 uppercase letters")
        String symbol,

        @NotBlank(message = "Company name is required")
        String companyName,

        @NotBlank(message = "Sector is required")
        String sector,

        @NotBlank(message = "Exchange is required")
        @Pattern(regexp = "LSE|NYSE|NASDAQ", message = "Exchange must be LSE, NYSE, or NASDAQ")
        String exchange
) {}
