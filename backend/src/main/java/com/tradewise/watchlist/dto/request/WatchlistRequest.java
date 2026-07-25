package com.tradewise.watchlist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Create / update a watchlist container (name + description + flags).
 */
public record WatchlistRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 80)
        String name,

        @Size(max = 500)
        String description,

        Boolean pinned,
        Boolean favorite
) {
}
