package com.tradewise.portfolio.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight recent-trade projection used on the dashboard (decoupled from the
 * trade module's own response DTO).
 */
public record RecentTradeResponse(
        UUID id,
        String stockSymbol,
        String tradeType,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal totalAmount,
        String status,
        Instant createdDate
) {
}
