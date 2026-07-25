package com.tradewise.portfolio.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single cell in the gain/loss calendar heatmap.
 */
public record HeatmapPointResponse(
        LocalDate date,
        BigDecimal profitLoss,
        long tradeCount
) {
}
