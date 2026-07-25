package com.tradewise.watchlist.domain;

import com.tradewise.watchlist.domain.enums.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * A single instrument tracked inside a {@link Watchlist}. Stored as a nested
 * value within the watchlist aggregate during the in-memory phase.
 */
@Getter
@Setter
@NoArgsConstructor
public class WatchlistItem {

    private UUID id = UUID.randomUUID();

    private String stockSymbol;
    private String company;
    private String exchange;
    private String sector;

    private BigDecimal currentPrice;
    private BigDecimal targetPrice;
    private BigDecimal stopLoss;

    private String notes;
    private Priority priority = Priority.MEDIUM;
    private String colorTag;
    private boolean alertEnabled = false;

    /** Zero-based position for drag-and-drop ordering. */
    private int position;

    private Instant createdDate = Instant.now();
    private Instant updatedDate = Instant.now();
}
