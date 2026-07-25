package com.tradewise.portfolio.domain;

import com.tradewise.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A user's portfolio. Each user owns exactly one. Holdings and performance are
 * derived on demand from {@code Trade} data, so only cash / capital metadata is
 * stored here.
 */
@Getter
@Setter
@NoArgsConstructor
public class Portfolio extends BaseEntity {

    private UUID userId;

    private String name;
    private String baseCurrency = "USD";

    /** Capital initially allocated to the portfolio. */
    private BigDecimal initialCapital = BigDecimal.ZERO;

    /** Un-invested cash currently available. */
    private BigDecimal availableCash = BigDecimal.ZERO;
}
