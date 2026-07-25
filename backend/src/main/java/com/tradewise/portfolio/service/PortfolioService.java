package com.tradewise.portfolio.service;

import com.tradewise.portfolio.dto.request.AdjustCashRequest;
import com.tradewise.portfolio.dto.request.CreatePortfolioRequest;
import com.tradewise.portfolio.dto.response.AllocationResponse;
import com.tradewise.portfolio.dto.response.DashboardResponse;
import com.tradewise.portfolio.dto.response.HoldingResponse;
import com.tradewise.portfolio.dto.response.PerformancePointResponse;
import com.tradewise.portfolio.dto.response.PortfolioResponse;
import com.tradewise.portfolio.dto.response.PortfolioSummaryResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Portfolio dashboard use cases. The dashboard values are derived on demand from
 * trades and watchlists — nothing is duplicated in storage.
 */
public interface PortfolioService {

    PortfolioResponse createPortfolio(UUID userId, CreatePortfolioRequest request);

    PortfolioResponse getPortfolio(UUID userId);

    PortfolioResponse adjustCash(UUID userId, AdjustCashRequest request);

    DashboardResponse getDashboard(UUID userId);

    PortfolioSummaryResponse getSummary(UUID userId);

    List<HoldingResponse> getHoldings(UUID userId);

    /** Returns asset and sector allocation keyed as {@code "asset"} / {@code "sector"}. */
    Map<String, List<AllocationResponse>> getAllocation(UUID userId);

    /** Returns performance series keyed as {@code monthly} / {@code yearly} / {@code timeline}. */
    Map<String, List<PerformancePointResponse>> getPerformance(UUID userId);
}
