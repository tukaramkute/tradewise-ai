package com.tradewise.trade.service.impl;

import com.tradewise.common.exception.ResourceNotFoundException;
import com.tradewise.common.pagination.PaginationUtils;
import com.tradewise.common.response.PagedResponse;
import com.tradewise.common.security.CurrentUserProvider;
import com.tradewise.trade.domain.Trade;
import com.tradewise.trade.domain.enums.TradeStatus;
import com.tradewise.trade.dto.request.CloseTradeRequest;
import com.tradewise.trade.dto.request.TradeFilter;
import com.tradewise.trade.dto.request.TradeRequest;
import com.tradewise.trade.dto.response.TradeResponse;
import com.tradewise.trade.dto.response.TradeStatisticsResponse;
import com.tradewise.trade.mapper.TradeMapper;
import com.tradewise.trade.repository.TradeRepository;
import com.tradewise.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Default {@link TradeService}. Owns the derivation of {@code totalAmount} and
 * all analytics. Ownership is enforced on every single-trade operation so users
 * can only touch their own trades.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private static final int MONEY_SCALE = 2;

    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public TradeResponse create(UUID userId, TradeRequest request) {
        Trade trade = tradeMapper.toEntity(request);
        trade.setUserId(userId);
        trade.setBrokerage(nvl(request.brokerage()));
        trade.setTaxes(nvl(request.taxes()));
        trade.setTotalAmount(computeTotal(trade));
        trade.setStatus(TradeStatus.OPEN);

        String actor = currentUserProvider.getCurrentUsername();
        Instant now = Instant.now();
        trade.setCreatedBy(actor);
        trade.setCreatedDate(now);
        trade.setUpdatedBy(actor);
        trade.setUpdatedDate(now);

        Trade saved = tradeRepository.save(trade);
        log.info("Trade created id={} user={} symbol={}", saved.getId(), userId, saved.getStockSymbol());
        return tradeMapper.toResponse(saved);
    }

    @Override
    public TradeResponse update(UUID userId, UUID tradeId, TradeRequest request) {
        Trade trade = getOwnedTrade(userId, tradeId);
        tradeMapper.updateEntity(request, trade);
        trade.setBrokerage(nvl(request.brokerage()));
        trade.setTaxes(nvl(request.taxes()));
        trade.setTotalAmount(computeTotal(trade));
        touch(trade);
        Trade saved = tradeRepository.save(trade);
        log.info("Trade updated id={}", tradeId);
        return tradeMapper.toResponse(saved);
    }

    @Override
    public TradeResponse close(UUID userId, UUID tradeId, CloseTradeRequest request) {
        Trade trade = getOwnedTrade(userId, tradeId);
        trade.setStatus(request.status() != null ? request.status() : TradeStatus.CLOSED);
        trade.setProfitLoss(request.profitLoss());
        touch(trade);
        Trade saved = tradeRepository.save(trade);
        log.info("Trade closed id={} pnl={}", tradeId, request.profitLoss());
        return tradeMapper.toResponse(saved);
    }

    @Override
    public TradeResponse getById(UUID userId, UUID tradeId) {
        return tradeMapper.toResponse(getOwnedTrade(userId, tradeId));
    }

    @Override
    public PagedResponse<TradeResponse> search(UUID userId, TradeFilter filter,
                                               int page, int size, String sortBy, String sortDir) {
        List<Trade> matched = tradeRepository.search(userId, filter);
        Comparator<Trade> comparator = buildComparator(sortBy, sortDir);
        List<Trade> pageItems = PaginationUtils.paginate(matched, page, size, comparator);
        List<TradeResponse> content = pageItems.stream().map(tradeMapper::toResponse).toList();
        return PagedResponse.of(content, page, size, matched.size());
    }

    @Override
    public void delete(UUID userId, UUID tradeId) {
        Trade trade = getOwnedTrade(userId, tradeId);
        trade.softDelete(currentUserProvider.getCurrentUsername());
        tradeRepository.save(trade);
        log.info("Trade soft-deleted id={}", tradeId);
    }

    @Override
    public TradeStatisticsResponse statistics(UUID userId, TradeFilter filter) {
        List<Trade> trades = tradeRepository.search(userId, filter);
        long total = trades.size();
        long open = trades.stream().filter(t -> t.getStatus() == TradeStatus.OPEN).count();
        long closed = trades.stream().filter(t -> t.getStatus() == TradeStatus.CLOSED).count();

        List<Trade> withPnl = trades.stream().filter(t -> t.getProfitLoss() != null).toList();
        List<Trade> wins = withPnl.stream().filter(t -> t.getProfitLoss().signum() > 0).toList();
        List<Trade> losses = withPnl.stream().filter(t -> t.getProfitLoss().signum() < 0).toList();

        BigDecimal totalProfit = sum(wins.stream().map(Trade::getProfitLoss).toList());
        BigDecimal totalLoss = sum(losses.stream().map(Trade::getProfitLoss).toList()).abs();
        BigDecimal net = sum(withPnl.stream().map(Trade::getProfitLoss).toList());

        BigDecimal winRate = withPnl.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(wins.size())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(withPnl.size()), MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal avgProfit = average(totalProfit, wins.size());
        BigDecimal avgLoss = average(totalLoss, losses.size());
        BigDecimal riskReward = avgLoss.signum() == 0 ? BigDecimal.ZERO
                : avgProfit.divide(avgLoss, MONEY_SCALE, RoundingMode.HALF_UP);

        Trade best = withPnl.stream().max(Comparator.comparing(Trade::getProfitLoss)).orElse(null);
        Trade worst = withPnl.stream().min(Comparator.comparing(Trade::getProfitLoss)).orElse(null);

        return new TradeStatisticsResponse(
                total, open, closed, wins.size(), losses.size(),
                winRate, totalProfit, totalLoss, net, avgProfit, avgLoss, riskReward,
                best == null ? null : new TradeStatisticsResponse.BestWorstTrade(best.getStockSymbol(), best.getProfitLoss()),
                worst == null ? null : new TradeStatisticsResponse.BestWorstTrade(worst.getStockSymbol(), worst.getProfitLoss()));
    }

    // ----- helpers -----

    private Trade getOwnedTrade(UUID userId, UUID tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> ResourceNotFoundException.of("Trade", tradeId));
        if (!trade.getUserId().equals(userId)) {
            // Do not disclose existence of another user's resource.
            throw ResourceNotFoundException.of("Trade", tradeId);
        }
        return trade;
    }

    private BigDecimal computeTotal(Trade trade) {
        BigDecimal gross = trade.getQuantity().multiply(trade.getPrice());
        return gross.add(nvl(trade.getBrokerage())).add(nvl(trade.getTaxes()))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private Comparator<Trade> buildComparator(String sortBy, String sortDir) {
        Comparator<Trade> comparator = switch (sortBy == null ? "tradeDate" : sortBy) {
            case "price" -> Comparator.comparing(Trade::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
            case "totalAmount" -> Comparator.comparing(Trade::getTotalAmount, Comparator.nullsLast(Comparator.naturalOrder()));
            case "stockSymbol" -> Comparator.comparing(Trade::getStockSymbol, Comparator.nullsLast(Comparator.naturalOrder()));
            case "createdDate" -> Comparator.comparing(Trade::getCreatedDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(Trade::getTradeDate, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return "asc".equalsIgnoreCase(sortDir) ? comparator : comparator.reversed();
    }

    private void touch(Trade trade) {
        trade.setUpdatedBy(currentUserProvider.getCurrentUsername());
        trade.setUpdatedDate(Instant.now());
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static BigDecimal sum(List<BigDecimal> values) {
        return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal average(BigDecimal total, int count) {
        return count == 0 ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(count), MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
