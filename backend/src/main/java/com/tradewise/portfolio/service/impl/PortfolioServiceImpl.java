package com.tradewise.portfolio.service.impl;

import com.tradewise.common.exception.BusinessRuleException;
import com.tradewise.common.exception.DuplicateResourceException;
import com.tradewise.common.exception.ResourceNotFoundException;
import com.tradewise.common.security.CurrentUserProvider;
import com.tradewise.portfolio.domain.Portfolio;
import com.tradewise.portfolio.dto.request.AdjustCashRequest;
import com.tradewise.portfolio.dto.request.CreatePortfolioRequest;
import com.tradewise.portfolio.dto.response.AllocationResponse;
import com.tradewise.portfolio.dto.response.DashboardResponse;
import com.tradewise.portfolio.dto.response.HeatmapPointResponse;
import com.tradewise.portfolio.dto.response.HoldingResponse;
import com.tradewise.portfolio.dto.response.PerformancePointResponse;
import com.tradewise.portfolio.dto.response.PortfolioResponse;
import com.tradewise.portfolio.dto.response.PortfolioSummaryResponse;
import com.tradewise.portfolio.dto.response.RecentTradeResponse;
import com.tradewise.portfolio.dto.response.RecentWatchlistItemResponse;
import com.tradewise.portfolio.mapper.PortfolioMapper;
import com.tradewise.portfolio.repository.PortfolioRepository;
import com.tradewise.portfolio.service.HoldingCalculator;
import com.tradewise.portfolio.service.PortfolioService;
import com.tradewise.trade.domain.Trade;
import com.tradewise.trade.domain.enums.TradeStatus;
import com.tradewise.trade.repository.TradeRepository;
import com.tradewise.watchlist.domain.Watchlist;
import com.tradewise.watchlist.domain.WatchlistItem;
import com.tradewise.watchlist.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default {@link PortfolioService}. Acts as the read-model aggregator for the
 * dashboard, composing data from the portfolio, trade and watchlist modules
 * without duplicating any of it in storage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private static final int MONEY_SCALE = 2;
    private static final int RECENT_LIMIT = 5;
    private static final String UNKNOWN_SECTOR = "UNKNOWN";
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    private final TradeRepository tradeRepository;
    private final WatchlistRepository watchlistRepository;
    private final HoldingCalculator holdingCalculator;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public PortfolioResponse createPortfolio(UUID userId, CreatePortfolioRequest request) {
        if (portfolioRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateResourceException("A portfolio already exists for this user");
        }
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setName(request.name());
        portfolio.setBaseCurrency(request.baseCurrency() == null ? "USD" : request.baseCurrency().toUpperCase());
        BigDecimal capital = request.initialCapital() == null ? BigDecimal.ZERO : request.initialCapital();
        portfolio.setInitialCapital(capital);
        portfolio.setAvailableCash(capital);

        String actor = currentUserProvider.getCurrentUsername();
        Instant now = Instant.now();
        portfolio.setCreatedBy(actor);
        portfolio.setCreatedDate(now);
        portfolio.setUpdatedBy(actor);
        portfolio.setUpdatedDate(now);

        Portfolio saved = portfolioRepository.save(portfolio);
        log.info("Portfolio created id={} user={}", saved.getId(), userId);
        return portfolioMapper.toResponse(saved);
    }

    @Override
    public PortfolioResponse getPortfolio(UUID userId) {
        return portfolioMapper.toResponse(requirePortfolio(userId));
    }

    @Override
    public PortfolioResponse adjustCash(UUID userId, AdjustCashRequest request) {
        Portfolio portfolio = requirePortfolio(userId);
        BigDecimal newCash = portfolio.getAvailableCash().add(request.amount());
        if (newCash.signum() < 0) {
            throw new BusinessRuleException("Withdrawal exceeds available cash");
        }
        portfolio.setAvailableCash(newCash);
        portfolio.setUpdatedBy(currentUserProvider.getCurrentUsername());
        portfolio.setUpdatedDate(Instant.now());
        return portfolioMapper.toResponse(portfolioRepository.save(portfolio));
    }

    @Override
    public DashboardResponse getDashboard(UUID userId) {
        Portfolio portfolio = requirePortfolio(userId);
        List<Trade> trades = tradeRepository.findAllByUserId(userId);
        List<Watchlist> watchlists = watchlistRepository.findAllByUserId(userId);
        Map<String, String> sectorMap = buildSectorMap(watchlists);
        List<HoldingResponse> holdings = holdingCalculator.calculate(trades, sectorMap);

        return new DashboardResponse(
                portfolioMapper.toResponse(portfolio),
                buildSummary(portfolio, trades, holdings),
                holdings,
                assetAllocation(holdings),
                sectorAllocation(holdings),
                recentTrades(trades),
                recentWatchlist(watchlists),
                cumulativeMonthly(trades),
                monthlyGrowth(trades),
                yearlyGrowth(trades),
                cumulativeTimeline(trades),
                heatmap(trades));
    }

    @Override
    public PortfolioSummaryResponse getSummary(UUID userId) {
        Portfolio portfolio = requirePortfolio(userId);
        List<Trade> trades = tradeRepository.findAllByUserId(userId);
        Map<String, String> sectorMap = buildSectorMap(watchlistRepository.findAllByUserId(userId));
        List<HoldingResponse> holdings = holdingCalculator.calculate(trades, sectorMap);
        return buildSummary(portfolio, trades, holdings);
    }

    @Override
    public List<HoldingResponse> getHoldings(UUID userId) {
        requirePortfolio(userId);
        List<Trade> trades = tradeRepository.findAllByUserId(userId);
        Map<String, String> sectorMap = buildSectorMap(watchlistRepository.findAllByUserId(userId));
        return holdingCalculator.calculate(trades, sectorMap);
    }

    @Override
    public Map<String, List<AllocationResponse>> getAllocation(UUID userId) {
        List<HoldingResponse> holdings = getHoldings(userId);
        Map<String, List<AllocationResponse>> result = new LinkedHashMap<>();
        result.put("asset", assetAllocation(holdings));
        result.put("sector", sectorAllocation(holdings));
        return result;
    }

    @Override
    public Map<String, List<PerformancePointResponse>> getPerformance(UUID userId) {
        requirePortfolio(userId);
        List<Trade> trades = tradeRepository.findAllByUserId(userId);
        Map<String, List<PerformancePointResponse>> result = new LinkedHashMap<>();
        result.put("monthly", monthlyGrowth(trades));
        result.put("yearly", yearlyGrowth(trades));
        result.put("timeline", cumulativeTimeline(trades));
        return result;
    }

    // ----- summary -----

    private PortfolioSummaryResponse buildSummary(Portfolio portfolio, List<Trade> trades,
                                                  List<HoldingResponse> holdings) {
        BigDecimal totalInvestment = sum(holdings.stream().map(HoldingResponse::investedAmount).toList());
        BigDecimal currentValue = sum(holdings.stream().map(HoldingResponse::currentValue).toList());
        BigDecimal unrealized = currentValue.subtract(totalInvestment);
        BigDecimal realized = sum(trades.stream()
                .map(Trade::getProfitLoss)
                .filter(java.util.Objects::nonNull)
                .toList());
        BigDecimal overall = unrealized.add(realized);
        BigDecimal profitPct = totalInvestment.signum() == 0 ? BigDecimal.ZERO
                : overall.multiply(BigDecimal.valueOf(100)).divide(totalInvestment, MONEY_SCALE, RoundingMode.HALF_UP);

        long open = trades.stream().filter(t -> t.getStatus() == TradeStatus.OPEN).count();
        long closed = trades.stream().filter(t -> t.getStatus() == TradeStatus.CLOSED).count();

        String top = holdings.stream()
                .max(Comparator.comparing(HoldingResponse::unrealizedPnlPercent,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(HoldingResponse::stockSymbol).orElse(null);
        String worst = holdings.stream()
                .min(Comparator.comparing(HoldingResponse::unrealizedPnlPercent,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(HoldingResponse::stockSymbol).orElse(null);

        return new PortfolioSummaryResponse(
                scale(totalInvestment), scale(currentValue), BigDecimal.ZERO.setScale(MONEY_SCALE),
                scale(overall), profitPct, scale(portfolio.getAvailableCash()),
                holdings.size(), open, closed, top, worst);
    }

    // ----- allocation -----

    private List<AllocationResponse> assetAllocation(List<HoldingResponse> holdings) {
        BigDecimal total = sum(holdings.stream().map(HoldingResponse::currentValue).toList());
        return holdings.stream()
                .map(h -> new AllocationResponse(h.stockSymbol(), h.currentValue(), percentage(h.currentValue(), total)))
                .toList();
    }

    private List<AllocationResponse> sectorAllocation(List<HoldingResponse> holdings) {
        Map<String, BigDecimal> bySector = new LinkedHashMap<>();
        for (HoldingResponse h : holdings) {
            String sector = h.sector() == null || h.sector().isBlank() ? UNKNOWN_SECTOR : h.sector();
            bySector.merge(sector, h.currentValue(), BigDecimal::add);
        }
        BigDecimal total = sum(bySector.values().stream().toList());
        return bySector.entrySet().stream()
                .map(e -> new AllocationResponse(e.getKey(), e.getValue(), percentage(e.getValue(), total)))
                .toList();
    }

    // ----- recent -----

    private List<RecentTradeResponse> recentTrades(List<Trade> trades) {
        return trades.stream()
                .sorted(Comparator.comparing(Trade::getCreatedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(RECENT_LIMIT)
                .map(t -> new RecentTradeResponse(t.getId(), t.getStockSymbol(),
                        t.getTradeType() == null ? null : t.getTradeType().name(),
                        t.getQuantity(), t.getPrice(), t.getTotalAmount(),
                        t.getStatus() == null ? null : t.getStatus().name(), t.getCreatedDate()))
                .toList();
    }

    private List<RecentWatchlistItemResponse> recentWatchlist(List<Watchlist> watchlists) {
        return watchlists.stream()
                .flatMap(w -> w.getItems().stream()
                        .map(i -> Map.entry(w, i)))
                .sorted(Comparator.comparing((Map.Entry<Watchlist, WatchlistItem> e) -> e.getValue().getCreatedDate(),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(RECENT_LIMIT)
                .map(e -> new RecentWatchlistItemResponse(
                        e.getKey().getId(), e.getKey().getName(),
                        e.getValue().getStockSymbol(), e.getValue().getCompany(),
                        e.getValue().getCurrentPrice(), e.getValue().getTargetPrice()))
                .toList();
    }

    // ----- performance -----

    private List<PerformancePointResponse> monthlyGrowth(List<Trade> trades) {
        Map<String, BigDecimal> byMonth = new TreeMap<>();
        for (Trade t : realizedTrades(trades)) {
            byMonth.merge(t.getTradeDate().format(MONTH_FMT), t.getProfitLoss(), BigDecimal::add);
        }
        return toPoints(byMonth);
    }

    private List<PerformancePointResponse> yearlyGrowth(List<Trade> trades) {
        Map<String, BigDecimal> byYear = new TreeMap<>();
        for (Trade t : realizedTrades(trades)) {
            byYear.merge(String.valueOf(t.getTradeDate().getYear()), t.getProfitLoss(), BigDecimal::add);
        }
        return toPoints(byYear);
    }

    private List<PerformancePointResponse> cumulativeMonthly(List<Trade> trades) {
        return cumulative(monthlyGrowth(trades));
    }

    private List<PerformancePointResponse> cumulativeTimeline(List<Trade> trades) {
        Map<String, BigDecimal> byDate = new TreeMap<>();
        for (Trade t : realizedTrades(trades)) {
            byDate.merge(t.getTradeDate().toString(), t.getProfitLoss(), BigDecimal::add);
        }
        return cumulative(toPoints(byDate));
    }

    private List<HeatmapPointResponse> heatmap(List<Trade> trades) {
        Map<LocalDate, BigDecimal> pnlByDate = new TreeMap<>();
        Map<LocalDate, Long> countByDate = new TreeMap<>();
        for (Trade t : realizedTrades(trades)) {
            pnlByDate.merge(t.getTradeDate(), t.getProfitLoss(), BigDecimal::add);
            countByDate.merge(t.getTradeDate(), 1L, Long::sum);
        }
        return pnlByDate.entrySet().stream()
                .map(e -> new HeatmapPointResponse(e.getKey(), scale(e.getValue()),
                        countByDate.getOrDefault(e.getKey(), 0L)))
                .toList();
    }

    // ----- helpers -----

    private Portfolio requirePortfolio(UUID userId) {
        return portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No portfolio found for this user"));
    }

    private Map<String, String> buildSectorMap(List<Watchlist> watchlists) {
        return watchlists.stream()
                .flatMap(w -> w.getItems().stream())
                .filter(i -> i.getStockSymbol() != null && i.getSector() != null && !i.getSector().isBlank())
                .collect(Collectors.toMap(
                        i -> i.getStockSymbol().toUpperCase(),
                        WatchlistItem::getSector,
                        (a, b) -> a));
    }

    private List<Trade> realizedTrades(List<Trade> trades) {
        return trades.stream()
                .filter(t -> t.getProfitLoss() != null && t.getTradeDate() != null)
                .toList();
    }

    private List<PerformancePointResponse> toPoints(Map<String, BigDecimal> data) {
        return data.entrySet().stream()
                .map(e -> new PerformancePointResponse(e.getKey(), scale(e.getValue())))
                .toList();
    }

    private List<PerformancePointResponse> cumulative(List<PerformancePointResponse> points) {
        BigDecimal running = BigDecimal.ZERO;
        List<PerformancePointResponse> result = new java.util.ArrayList<>();
        for (PerformancePointResponse p : points) {
            running = running.add(p.value());
            result.add(new PerformancePointResponse(p.period(), scale(running)));
        }
        return result;
    }

    private BigDecimal percentage(BigDecimal value, BigDecimal total) {
        if (value == null || total == null || total.signum() == 0) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE);
        }
        return value.multiply(BigDecimal.valueOf(100)).divide(total, MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal sum(List<BigDecimal> values) {
        return values.stream().filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal scale(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(MONEY_SCALE) : value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
