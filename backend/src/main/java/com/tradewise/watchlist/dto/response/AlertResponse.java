package com.tradewise.watchlist.dto.response;

import com.tradewise.watchlist.domain.enums.AlertType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A triggered alert produced when a supplied market price crosses an item's
 * target / stop-loss / configured threshold.
 */
public record AlertResponse(
        UUID itemId,
        String stockSymbol,
        AlertType alertType,
        BigDecimal referencePrice,
        BigDecimal marketPrice,
        String message
) {
}
