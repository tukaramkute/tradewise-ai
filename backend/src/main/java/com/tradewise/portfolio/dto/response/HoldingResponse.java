package com.tradewise.portfolio.dto.response;

import java.math.BigDecimal;

/**
 * A derived net position for a single instrument.
 */
public record HoldingResponse(
        String stockSymbol,
        String companyName,
        String exchange,
        String sector,
        BigDecimal quantity,
        BigDecimal averageBuyPrice,
        BigDecimal investedAmount,
        BigDecimal currentPrice,
        BigDecimal currentValue,
        BigDecimal unrealizedPnl,
        BigDecimal unrealizedPnlPercent
) {
}
