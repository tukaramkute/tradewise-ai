package com.tradewise.watchlist.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/**
 * Persists a new drag-and-drop ordering of items within a watchlist. The list
 * is the item ids in their desired display order.
 */
public record ReorderItemsRequest(
        @NotEmpty(message = "Ordered item id list is required")
        List<UUID> orderedItemIds
) {
}
