package com.tradewise.trade.repository;

import com.tradewise.common.repository.InMemoryCrudRepository;
import com.tradewise.trade.domain.Trade;
import com.tradewise.trade.dto.request.TradeFilter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * In-memory {@link TradeRepository}. The {@link #search} method composes
 * predicates for each supplied filter field, mirroring what a JPA
 * {@code Specification} would later express.
 */
@Repository
public class InMemoryTradeRepository extends InMemoryCrudRepository<Trade> implements TradeRepository {

    @Override
    public List<Trade> findAllByUserId(UUID userId) {
        return findActiveBy(t -> userId.equals(t.getUserId()));
    }

    @Override
    public List<Trade> search(UUID userId, TradeFilter filter) {
        Predicate<Trade> predicate = t -> userId.equals(t.getUserId());
        if (filter != null) {
            predicate = predicate.and(matches(filter));
        }
        return findActiveBy(predicate);
    }

    private Predicate<Trade> matches(TradeFilter f) {
        Predicate<Trade> p = t -> true;
        if (f.search() != null && !f.search().isBlank()) {
            String q = f.search().toLowerCase();
            p = p.and(t -> (t.getStockSymbol() != null && t.getStockSymbol().toLowerCase().contains(q))
                    || (t.getCompanyName() != null && t.getCompanyName().toLowerCase().contains(q)));
        }
        if (f.stockSymbol() != null && !f.stockSymbol().isBlank()) {
            p = p.and(t -> f.stockSymbol().equalsIgnoreCase(t.getStockSymbol()));
        }
        if (f.exchange() != null && !f.exchange().isBlank()) {
            p = p.and(t -> f.exchange().equalsIgnoreCase(t.getExchange()));
        }
        if (f.tradeType() != null) {
            p = p.and(t -> f.tradeType() == t.getTradeType());
        }
        if (f.orderType() != null) {
            p = p.and(t -> f.orderType() == t.getOrderType());
        }
        if (f.status() != null) {
            p = p.and(t -> f.status() == t.getStatus());
        }
        if (f.fromDate() != null) {
            p = p.and(t -> t.getTradeDate() != null && !t.getTradeDate().isBefore(f.fromDate()));
        }
        if (f.toDate() != null) {
            p = p.and(t -> t.getTradeDate() != null && !t.getTradeDate().isAfter(f.toDate()));
        }
        return p;
    }
}
