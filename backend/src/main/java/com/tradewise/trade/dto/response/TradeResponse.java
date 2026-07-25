package com.tradewise.trade.dto.response;

import com.tradewise.trade.domain.enums.OrderType;
import com.tradewise.trade.domain.enums.TradeStatus;
import com.tradewise.trade.domain.enums.TradeType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TradeResponse(
        UUID id,
        UUID userId,
        String stockSymbol,
        String companyName,
        String exchange,
        TradeType tradeType,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal brokerage,
        BigDecimal taxes,
        BigDecimal totalAmount,
        OrderType orderType,
        LocalDate tradeDate,
        LocalDate settlementDate,
        TradeStatus status,
        BigDecimal profitLoss,
        String notes,
        String attachmentUrl,
        Instant createdDate,
        Instant updatedDate
) {
}
