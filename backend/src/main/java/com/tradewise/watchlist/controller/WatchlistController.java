package com.tradewise.watchlist.controller;

import com.tradewise.common.response.ApiResponse;
import com.tradewise.watchlist.dto.request.ReorderItemsRequest;
import com.tradewise.watchlist.dto.request.WatchlistItemRequest;
import com.tradewise.watchlist.dto.request.WatchlistRequest;
import com.tradewise.watchlist.dto.response.AlertResponse;
import com.tradewise.watchlist.dto.response.WatchlistResponse;
import com.tradewise.watchlist.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Watchlist endpoints: CRUD, pin/favorite, item management, reorder and alerts.
 */
@RestController
@RequestMapping("/api/v1/watchlists")
@RequiredArgsConstructor
@Tag(name = "Watchlists", description = "Watchlist and item management with price alerts")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @Operation(summary = "Create a watchlist")
    @PostMapping
    public ResponseEntity<ApiResponse<WatchlistResponse>> create(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody WatchlistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(watchlistService.create(userId, request), "Watchlist created"));
    }

    @Operation(summary = "List watchlists (search + sort)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<WatchlistResponse>>> list(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sortBy) {
        return ResponseEntity.ok(ApiResponse.success(watchlistService.getAll(userId, search, sortBy)));
    }

    @Operation(summary = "Get a watchlist by id")
    @GetMapping("/{watchlistId}")
    public ResponseEntity<ApiResponse<WatchlistResponse>> get(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId) {
        return ResponseEntity.ok(ApiResponse.success(watchlistService.getById(userId, watchlistId)));
    }

    @Operation(summary = "Update a watchlist")
    @PutMapping("/{watchlistId}")
    public ResponseEntity<ApiResponse<WatchlistResponse>> update(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId,
            @Valid @RequestBody WatchlistRequest request) {
        return ResponseEntity.ok(ApiResponse.success(watchlistService.update(userId, watchlistId, request), "Watchlist updated"));
    }

    @Operation(summary = "Delete a watchlist (soft delete)")
    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId) {
        watchlistService.delete(userId, watchlistId);
        return ResponseEntity.ok(ApiResponse.success(null, "Watchlist deleted"));
    }

    @Operation(summary = "Toggle pin")
    @PatchMapping("/{watchlistId}/pin")
    public ResponseEntity<ApiResponse<WatchlistResponse>> togglePin(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId) {
        return ResponseEntity.ok(ApiResponse.success(watchlistService.togglePin(userId, watchlistId)));
    }

    @Operation(summary = "Toggle favorite")
    @PatchMapping("/{watchlistId}/favorite")
    public ResponseEntity<ApiResponse<WatchlistResponse>> toggleFavorite(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId) {
        return ResponseEntity.ok(ApiResponse.success(watchlistService.toggleFavorite(userId, watchlistId)));
    }

    @Operation(summary = "Add an item to a watchlist")
    @PostMapping("/{watchlistId}/items")
    public ResponseEntity<ApiResponse<WatchlistResponse>> addItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId,
            @Valid @RequestBody WatchlistItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(watchlistService.addItem(userId, watchlistId, request), "Item added"));
    }

    @Operation(summary = "Update a watchlist item")
    @PutMapping("/{watchlistId}/items/{itemId}")
    public ResponseEntity<ApiResponse<WatchlistResponse>> updateItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId,
            @PathVariable UUID itemId,
            @Valid @RequestBody WatchlistItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(watchlistService.updateItem(userId, watchlistId, itemId, request), "Item updated"));
    }

    @Operation(summary = "Remove a watchlist item")
    @DeleteMapping("/{watchlistId}/items/{itemId}")
    public ResponseEntity<ApiResponse<WatchlistResponse>> removeItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId,
            @PathVariable UUID itemId) {
        return ResponseEntity.ok(ApiResponse.success(watchlistService.removeItem(userId, watchlistId, itemId), "Item removed"));
    }

    @Operation(summary = "Reorder items (drag and drop)")
    @PatchMapping("/{watchlistId}/items/reorder")
    public ResponseEntity<ApiResponse<WatchlistResponse>> reorder(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId,
            @Valid @RequestBody ReorderItemsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(watchlistService.reorderItems(userId, watchlistId, request), "Items reordered"));
    }

    @Operation(summary = "Evaluate price alerts for an item against a market price")
    @GetMapping("/{watchlistId}/items/{itemId}/alerts")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> alerts(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID watchlistId,
            @PathVariable UUID itemId,
            @RequestParam BigDecimal marketPrice) {
        return ResponseEntity.ok(ApiResponse.success(
                watchlistService.evaluateAlerts(userId, watchlistId, itemId, marketPrice)));
    }
}
