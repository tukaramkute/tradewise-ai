package com.tradewise.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Creates a user's (single) portfolio.
 */
public record CreatePortfolioRequest(
        @NotBlank(message = "Portfolio name is required")
        @Size(max = 80)
        String name,

        @Size(min = 3, max = 3, message = "Base currency must be a 3-letter ISO code")
        String baseCurrency,

        @PositiveOrZero(message = "Initial capital cannot be negative")
        BigDecimal initialCapital
) {
}
