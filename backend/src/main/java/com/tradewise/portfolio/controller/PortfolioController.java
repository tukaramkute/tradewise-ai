package com.tradewise.portfolio.controller;

import com.tradewise.common.response.ApiResponse;
import com.tradewise.portfolio.dto.request.AdjustCashRequest;
import com.tradewise.portfolio.dto.request.CreatePortfolioRequest;
import com.tradewise.portfolio.dto.response.AllocationResponse;
import com.tradewise.portfolio.dto.response.DashboardResponse;
import com.tradewise.portfolio.dto.response.HoldingResponse;
import com.tradewise.portfolio.dto.response.PerformancePointResponse;
import com.tradewise.portfolio.dto.response.PortfolioResponse;
import com.tradewise.portfolio.dto.response.PortfolioSummaryResponse;
import com.tradewise.portfolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Portfolio dashboard endpoints.
 */
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "Portfolio dashboard, summary, holdings, allocation and performance")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "Create the user's portfolio")
    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioResponse>> create(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreatePortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(portfolioService.createPortfolio(userId, request), "Portfolio created"));
    }

    @Operation(summary = "Get portfolio metadata")
    @GetMapping
    public ResponseEntity<ApiResponse<PortfolioResponse>> get(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getPortfolio(userId)));
    }

    @Operation(summary = "Adjust available cash (deposit/withdraw)")
    @PatchMapping("/cash")
    public ResponseEntity<ApiResponse<PortfolioResponse>> adjustCash(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody AdjustCashRequest request) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.adjustCash(userId, request), "Cash updated"));
    }

    @Operation(summary = "Get the full dashboard")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getDashboard(userId)));
    }

    @Operation(summary = "Get the portfolio KPI summary")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> summary(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getSummary(userId)));
    }

    @Operation(summary = "Get current holdings")
    @GetMapping("/holdings")
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> holdings(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getHoldings(userId)));
    }

    @Operation(summary = "Get asset and sector allocation")
    @GetMapping("/allocation")
    public ResponseEntity<ApiResponse<Map<String, List<AllocationResponse>>>> allocation(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getAllocation(userId)));
    }

    @Operation(summary = "Get performance series (monthly, yearly, timeline)")
    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<Map<String, List<PerformancePointResponse>>>> performance(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getPerformance(userId)));
    }
}
