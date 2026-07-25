package com.tradewise.trade.dto.request;

import com.tradewise.trade.domain.enums.OrderType;
import com.tradewise.trade.domain.enums.TradeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload to create or fully update a trade. Derived fields (totalAmount,
 * profitLoss) are computed by the service and never accepted from the client.
 */
public record TradeRequest(
        @NotBlank(message = "Stock symbol is required")
        @Size(max = 20)
        String stockSymbol,

        @Size(max = 120)
        String companyName,

        @Size(max = 20)
        String exchange,

        @NotNull(message = "Trade type is required")
        TradeType tradeType,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
        BigDecimal quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price cannot be negative")
        BigDecimal price,

        @PositiveOrZero(message = "Brokerage cannot be negative")
        BigDecimal brokerage,

        @PositiveOrZero(message = "Taxes cannot be negative")
        BigDecimal taxes,

        @NotNull(message = "Order type is required")
        OrderType orderType,

        @NotNull(message = "Trade date is required")
        LocalDate tradeDate,

        LocalDate settlementDate,

        @Size(max = 2000)
        String notes,

        String attachmentUrl
) {
}
