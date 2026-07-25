package com.tradewise.portfolio.dto.response;

import java.math.BigDecimal;

/**
 * High-level portfolio summary — the KPI tiles at the top of the dashboard.
 */
public record PortfolioSummaryResponse(
        BigDecimal totalInvestment,
        BigDecimal currentPortfolioValue,
        BigDecimal todaysProfitLoss,
        BigDecimal overallProfitLoss,
        BigDecimal profitPercentage,
        BigDecimal availableCash,
        int numberOfHoldings,
        long openPositions,
        long closedPositions,
        String topPerformingStock,
        String worstPerformingStock
) {
}
