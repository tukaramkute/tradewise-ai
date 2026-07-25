package com.tradewise.trade.controller;

import com.tradewise.common.response.ApiResponse;
import com.tradewise.common.response.PagedResponse;
import com.tradewise.trade.domain.enums.OrderType;
import com.tradewise.trade.domain.enums.TradeStatus;
import com.tradewise.trade.domain.enums.TradeType;
import com.tradewise.trade.dto.request.CloseTradeRequest;
import com.tradewise.trade.dto.request.TradeFilter;
import com.tradewise.trade.dto.request.TradeRequest;
import com.tradewise.trade.dto.response.TradeResponse;
import com.tradewise.trade.dto.response.TradeStatisticsResponse;
import com.tradewise.trade.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Trade History endpoints: CRUD, search/filter/pagination/sort and analytics.
 */
@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
@Tag(name = "Trades", description = "Trade history management and analytics")
public class TradeController {

    private final TradeService tradeService;

    @Operation(summary = "Create a trade")
    @PostMapping
    public ResponseEntity<ApiResponse<TradeResponse>> create(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tradeService.create(userId, request), "Trade created"));
    }

    @Operation(summary = "Update a trade")
    @PutMapping("/{tradeId}")
    public ResponseEntity<ApiResponse<TradeResponse>> update(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID tradeId,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tradeService.update(userId, tradeId, request), "Trade updated"));
    }

    @Operation(summary = "Close a trade and record realised P/L")
    @PatchMapping("/{tradeId}/close")
    public ResponseEntity<ApiResponse<TradeResponse>> close(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID tradeId,
            @RequestBody CloseTradeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tradeService.close(userId, tradeId, request), "Trade closed"));
    }

    @Operation(summary = "Get a trade by id")
    @GetMapping("/{tradeId}")
    public ResponseEntity<ApiResponse<TradeResponse>> get(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID tradeId) {
        return ResponseEntity.ok(ApiResponse.success(tradeService.getById(userId, tradeId)));
    }

    @Operation(summary = "Search / filter / paginate trades")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TradeResponse>>> search(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String stockSymbol,
            @RequestParam(required = false) String exchange,
            @RequestParam(required = false) TradeType tradeType,
            @RequestParam(required = false) OrderType orderType,
            @RequestParam(required = false) TradeStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "tradeDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        TradeFilter filter = new TradeFilter(search, stockSymbol, exchange, tradeType, orderType, status, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(
                tradeService.search(userId, filter, page, size, sortBy, sortDir)));
    }

    @Operation(summary = "Delete a trade (soft delete)")
    @DeleteMapping("/{tradeId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID tradeId) {
        tradeService.delete(userId, tradeId);
        return ResponseEntity.ok(ApiResponse.success(null, "Trade deleted"));
    }

    @Operation(summary = "Get aggregated trade statistics")
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<TradeStatisticsResponse>> statistics(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) TradeStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        TradeFilter filter = new TradeFilter(null, null, null, null, null, status, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(tradeService.statistics(userId, filter)));
    }
}
