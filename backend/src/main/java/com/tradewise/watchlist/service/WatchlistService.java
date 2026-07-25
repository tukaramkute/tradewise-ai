package com.tradewise.watchlist.service;

import com.tradewise.watchlist.dto.request.ReorderItemsRequest;
import com.tradewise.watchlist.dto.request.WatchlistItemRequest;
import com.tradewise.watchlist.dto.request.WatchlistRequest;
import com.tradewise.watchlist.dto.response.AlertResponse;
import com.tradewise.watchlist.dto.response.WatchlistResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Watchlist use cases: CRUD, pin/favorite, item management, drag-and-drop
 * reordering and price-alert evaluation.
 */
public interface WatchlistService {

    WatchlistResponse create(UUID userId, WatchlistRequest request);

    WatchlistResponse update(UUID userId, UUID watchlistId, WatchlistRequest request);

    List<WatchlistResponse> getAll(UUID userId, String search, String sortBy);

    WatchlistResponse getById(UUID userId, UUID watchlistId);

    void delete(UUID userId, UUID watchlistId);

    WatchlistResponse togglePin(UUID userId, UUID watchlistId);

    WatchlistResponse toggleFavorite(UUID userId, UUID watchlistId);

    // ----- Items -----

    WatchlistResponse addItem(UUID userId, UUID watchlistId, WatchlistItemRequest request);

    WatchlistResponse updateItem(UUID userId, UUID watchlistId, UUID itemId, WatchlistItemRequest request);

    WatchlistResponse removeItem(UUID userId, UUID watchlistId, UUID itemId);

    WatchlistResponse reorderItems(UUID userId, UUID watchlistId, ReorderItemsRequest request);

    // ----- Alerts -----

    List<AlertResponse> evaluateAlerts(UUID userId, UUID watchlistId, UUID itemId, BigDecimal marketPrice);
}
