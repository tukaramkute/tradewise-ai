package com.tradewise.portfolio.dto.response;

import java.util.List;

/**
 * The full dashboard payload assembled from portfolio, trades and watchlists.
 */
public record DashboardResponse(
        PortfolioResponse portfolio,
        PortfolioSummaryResponse summary,
        List<HoldingResponse> holdings,
        List<AllocationResponse> assetAllocation,
        List<AllocationResponse> sectorAllocation,
        List<RecentTradeResponse> recentTrades,
        List<RecentWatchlistItemResponse> recentWatchlist,
        List<PerformancePointResponse> performanceChart,
        List<PerformancePointResponse> monthlyGrowth,
        List<PerformancePointResponse> yearlyGrowth,
        List<PerformancePointResponse> portfolioTimeline,
        List<HeatmapPointResponse> gainLossHeatmap
) {
}
