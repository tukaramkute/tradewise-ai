package com.tradewise.portfolio.service;

import com.tradewise.portfolio.dto.response.HoldingResponse;
import com.tradewise.trade.domain.Trade;
import com.tradewise.trade.domain.enums.TradeType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Derives current net holdings from a user's trade history.
 *
 * <p>Because Phase 1 has no live market-data feed, the "current price" is the
 * most recent recorded trade price for each symbol. When a real quote service is
 * added later, only this class changes.</p>
 */
@Component
public class HoldingCalculator {

    private static final int MONEY_SCALE = 2;
    private static final int QTY_SCALE = 4;

    /**
     * @param trades    all active trades for a user
     * @param sectorMap optional symbol -&gt; sector hints (e.g. from watchlists)
     */
    public List<HoldingResponse> calculate(List<Trade> trades, Map<String, String> sectorMap) {
        Map<String, List<Trade>> bySymbol = trades.stream()
                .filter(t -> t.getStockSymbol() != null)
                .collect(Collectors.groupingBy(t -> t.getStockSymbol().toUpperCase()));

        List<HoldingResponse> holdings = new ArrayList<>();
        for (var entry : bySymbol.entrySet()) {
            HoldingResponse holding = computeHolding(entry.getKey(), entry.getValue(), sectorMap);
            if (holding != null) {
                holdings.add(holding);
            }
        }
        holdings.sort(Comparator.comparing(HoldingResponse::currentValue,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return holdings;
    }

    private HoldingResponse computeHolding(String symbol, List<Trade> symbolTrades, Map<String, String> sectorMap) {
        BigDecimal buyQty = BigDecimal.ZERO;
        BigDecimal sellQty = BigDecimal.ZERO;
        BigDecimal buyCost = BigDecimal.ZERO;

        for (Trade t : symbolTrades) {
            if (t.getQuantity() == null || t.getPrice() == null) {
                continue;
            }
            if (t.getTradeType() == TradeType.BUY) {
                buyQty = buyQty.add(t.getQuantity());
                buyCost = buyCost.add(t.getQuantity().multiply(t.getPrice()));
            } else if (t.getTradeType() == TradeType.SELL) {
                sellQty = sellQty.add(t.getQuantity());
            }
        }

        BigDecimal netQty = buyQty.subtract(sellQty);
        if (netQty.signum() <= 0 || buyQty.signum() == 0) {
            return null; // fully exited or short — not an open long holding
        }

        BigDecimal avgBuyPrice = buyCost.divide(buyQty, QTY_SCALE, RoundingMode.HALF_UP);
        BigDecimal invested = netQty.multiply(avgBuyPrice).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal currentPrice = latestPrice(symbolTrades, avgBuyPrice);
        BigDecimal currentValue = netQty.multiply(currentPrice).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal unrealized = currentValue.subtract(invested);
        BigDecimal unrealizedPct = invested.signum() == 0 ? BigDecimal.ZERO
                : unrealized.multiply(BigDecimal.valueOf(100)).divide(invested, MONEY_SCALE, RoundingMode.HALF_UP);

        Trade sample = symbolTrades.get(0);
        String sector = sectorMap == null ? null : sectorMap.get(symbol);

        return new HoldingResponse(
                symbol,
                sample.getCompanyName(),
                sample.getExchange(),
                sector,
                netQty.setScale(QTY_SCALE, RoundingMode.HALF_UP),
                avgBuyPrice,
                invested,
                currentPrice,
                currentValue,
                unrealized,
                unrealizedPct);
    }

    private BigDecimal latestPrice(List<Trade> symbolTrades, BigDecimal fallback) {
        return symbolTrades.stream()
                .filter(t -> t.getPrice() != null && t.getTradeDate() != null)
                .max(Comparator.comparing(Trade::getTradeDate)
                        .thenComparing(t -> t.getCreatedDate() == null ? java.time.Instant.EPOCH : t.getCreatedDate()))
                .map(Trade::getPrice)
                .orElse(fallback);
    }
}
