package com.tradewise.portfolio.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PortfolioResponse(
        UUID id,
        UUID userId,
        String name,
        String baseCurrency,
        BigDecimal initialCapital,
        BigDecimal availableCash,
        Instant createdDate,
        Instant updatedDate
) {
}
