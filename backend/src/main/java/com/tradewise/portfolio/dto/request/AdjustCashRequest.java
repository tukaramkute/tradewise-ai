package com.tradewise.portfolio.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Adjusts available cash. A positive amount deposits, a negative amount
 * withdraws; the service rejects withdrawals that exceed available cash.
 */
public record AdjustCashRequest(
        @NotNull(message = "Amount is required")
        BigDecimal amount
) {
}
