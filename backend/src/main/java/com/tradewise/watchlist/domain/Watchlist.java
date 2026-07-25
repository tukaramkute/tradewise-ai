package com.tradewise.watchlist.domain;

import com.tradewise.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Watchlist aggregate. A user may own several; exactly one is the default.
 */
@Getter
@Setter
@NoArgsConstructor
public class Watchlist extends BaseEntity {

    private UUID userId;

    private String name;
    private String description;

    private boolean pinned = false;
    private boolean favorite = false;
    private boolean defaultWatchlist = false;

    private List<WatchlistItem> items = new ArrayList<>();
}
