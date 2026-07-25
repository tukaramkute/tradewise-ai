package com.tradewise.trade.dto.request;

import com.tradewise.trade.domain.enums.TradeStatus;

/**
 * Marks a trade as closed and records the realised P/L.
 */
public record CloseTradeRequest(
        java.math.BigDecimal profitLoss,
        TradeStatus status
) {
}
