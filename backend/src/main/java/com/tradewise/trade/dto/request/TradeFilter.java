package com.tradewise.trade.dto.request;

import com.tradewise.trade.domain.enums.OrderType;
import com.tradewise.trade.domain.enums.TradeStatus;
import com.tradewise.trade.domain.enums.TradeType;

import java.time.LocalDate;

/**
 * Search / filter criteria for listing trades. All fields optional; null values
 * are ignored. {@code search} matches symbol or company name (case-insensitive).
 */
public record TradeFilter(
        String search,
        String stockSymbol,
        String exchange,
        TradeType tradeType,
        OrderType orderType,
        TradeStatus status,
        LocalDate fromDate,
        LocalDate toDate
) {
}
