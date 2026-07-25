package com.tradewise.portfolio.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lightweight recent-watchlist-item projection used on the dashboard.
 */
public record RecentWatchlistItemResponse(
        UUID watchlistId,
        String watchlistName,
        String stockSymbol,
        String company,
        BigDecimal currentPrice,
        BigDecimal targetPrice
) {
}
