package com.tradewise.watchlist.dto.response;

import com.tradewise.watchlist.domain.enums.Priority;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WatchlistItemResponse(
        UUID id,
        String stockSymbol,
        String company,
        String exchange,
        String sector,
        BigDecimal currentPrice,
        BigDecimal targetPrice,
        BigDecimal stopLoss,
        String notes,
        Priority priority,
        String colorTag,
        boolean alertEnabled,
        int position,
        Instant createdDate,
        Instant updatedDate
) {
}
