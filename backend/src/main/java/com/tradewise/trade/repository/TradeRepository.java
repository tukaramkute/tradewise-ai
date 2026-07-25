package com.tradewise.trade.repository;

import com.tradewise.common.repository.CrudRepository;
import com.tradewise.trade.domain.Trade;
import com.tradewise.trade.dto.request.TradeFilter;

import java.util.List;
import java.util.UUID;

/**
 * Persistence contract for {@link Trade}.
 */
public interface TradeRepository extends CrudRepository<Trade> {

    List<Trade> findAllByUserId(UUID userId);

    /** Returns active trades for a user matching the (nullable) filter. */
    List<Trade> search(UUID userId, TradeFilter filter);
}
