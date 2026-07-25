package com.tradewise.watchlist.dto.request;

import com.tradewise.watchlist.domain.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Add / update an item inside a watchlist.
 */
public record WatchlistItemRequest(
        @NotBlank(message = "Stock symbol is required")
        @Size(max = 20)
        String stockSymbol,

        @Size(max = 120)
        String company,

        @Size(max = 20)
        String exchange,

        @Size(max = 60)
        String sector,

        @PositiveOrZero(message = "Current price cannot be negative")
        BigDecimal currentPrice,

        @PositiveOrZero(message = "Target price cannot be negative")
        BigDecimal targetPrice,

        @PositiveOrZero(message = "Stop loss cannot be negative")
        BigDecimal stopLoss,

        @Size(max = 1000)
        String notes,

        Priority priority,
        String colorTag,
        Boolean alertEnabled
) {
}
