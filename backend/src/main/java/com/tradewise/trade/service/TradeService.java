package com.tradewise.trade.service;

import com.tradewise.common.response.PagedResponse;
import com.tradewise.trade.dto.request.CloseTradeRequest;
import com.tradewise.trade.dto.request.TradeFilter;
import com.tradewise.trade.dto.request.TradeRequest;
import com.tradewise.trade.dto.response.TradeResponse;
import com.tradewise.trade.dto.response.TradeStatisticsResponse;

import java.util.UUID;

/**
 * Trade management use cases: CRUD, search/filter/pagination and analytics.
 */
public interface TradeService {

    TradeResponse create(UUID userId, TradeRequest request);

    TradeResponse update(UUID userId, UUID tradeId, TradeRequest request);

    TradeResponse close(UUID userId, UUID tradeId, CloseTradeRequest request);

    TradeResponse getById(UUID userId, UUID tradeId);

    PagedResponse<TradeResponse> search(UUID userId, TradeFilter filter,
                                        int page, int size, String sortBy, String sortDir);

    void delete(UUID userId, UUID tradeId);

    TradeStatisticsResponse statistics(UUID userId, TradeFilter filter);
}
