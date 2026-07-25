package com.tradewise.watchlist.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WatchlistResponse(
        UUID id,
        UUID userId,
        String name,
        String description,
        boolean pinned,
        boolean favorite,
        boolean defaultWatchlist,
        int itemCount,
        List<WatchlistItemResponse> items,
        Instant createdDate,
        Instant updatedDate
) {
}
