package com.tradewise.portfolio.dto.response;

import java.math.BigDecimal;

/**
 * A single point on a time-series (performance chart, growth, timeline).
 *
 * @param period label for the bucket (e.g. "2026-07", "2026" or a date)
 * @param value  realised P/L or cumulative value for that bucket
 */
public record PerformancePointResponse(
        String period,
        BigDecimal value
) {
}
