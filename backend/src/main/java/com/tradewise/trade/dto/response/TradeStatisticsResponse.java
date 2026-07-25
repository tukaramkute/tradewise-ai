package com.tradewise.trade.dto.response;

import java.math.BigDecimal;

/**
 * Aggregated trade statistics for the analytics endpoints.
 */
public record TradeStatisticsResponse(
        long totalTrades,
        long openTrades,
        long closedTrades,
        long winningTrades,
        long losingTrades,
        BigDecimal winRate,
        BigDecimal totalProfit,
        BigDecimal totalLoss,
        BigDecimal netProfitLoss,
        BigDecimal averageProfit,
        BigDecimal averageLoss,
        BigDecimal riskRewardRatio,
        BestWorstTrade bestTrade,
        BestWorstTrade worstTrade
) {
    public record BestWorstTrade(String stockSymbol, BigDecimal profitLoss) {
    }
}
