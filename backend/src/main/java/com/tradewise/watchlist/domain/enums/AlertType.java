package com.tradewise.watchlist.domain.enums;

/**
 * Types of price-based alerts that a watchlist item can trigger.
 */
public enum AlertType {
    PRICE_ABOVE,
    PRICE_BELOW,
    TARGET_HIT,
    STOP_LOSS_HIT
}
