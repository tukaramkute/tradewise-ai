package com.tradewise.portfolio.dto.response;

import java.math.BigDecimal;

/**
 * One slice of an allocation breakdown (asset or sector).
 */
public record AllocationResponse(
        String label,
        BigDecimal value,
        BigDecimal percentage
) {
}
