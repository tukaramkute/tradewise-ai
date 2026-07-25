package com.tradewise.trade.domain;

import com.tradewise.common.domain.BaseEntity;
import com.tradewise.trade.domain.enums.OrderType;
import com.tradewise.trade.domain.enums.TradeStatus;
import com.tradewise.trade.domain.enums.TradeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A single executed trade owned by a user. Monetary values use {@link BigDecimal}
 * to avoid floating-point rounding errors.
 */
@Getter
@Setter
@NoArgsConstructor
public class Trade extends BaseEntity {

    /** Owning user id. */
    private UUID userId;

    private String stockSymbol;
    private String companyName;
    private String exchange;

    private TradeType tradeType;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal brokerage = BigDecimal.ZERO;
    private BigDecimal taxes = BigDecimal.ZERO;

    /** Gross + charges (computed in the service). */
    private BigDecimal totalAmount;

    private OrderType orderType;
    private LocalDate tradeDate;
    private LocalDate settlementDate;

    private TradeStatus status = TradeStatus.OPEN;

    /** Realised P/L for closed trades; null while open. */
    private BigDecimal profitLoss;

    private String notes;
    private String attachmentUrl;
}
