package com.tradewise.watchlist.service.impl;

import com.tradewise.common.exception.BusinessRuleException;
import com.tradewise.common.exception.ResourceNotFoundException;
import com.tradewise.common.security.CurrentUserProvider;
import com.tradewise.watchlist.domain.Watchlist;
import com.tradewise.watchlist.domain.WatchlistItem;
import com.tradewise.watchlist.domain.enums.AlertType;
import com.tradewise.watchlist.domain.enums.Priority;
import com.tradewise.watchlist.dto.request.ReorderItemsRequest;
import com.tradewise.watchlist.dto.request.WatchlistItemRequest;
import com.tradewise.watchlist.dto.request.WatchlistRequest;
import com.tradewise.watchlist.dto.response.AlertResponse;
import com.tradewise.watchlist.dto.response.WatchlistResponse;
import com.tradewise.watchlist.mapper.WatchlistMapper;
import com.tradewise.watchlist.repository.WatchlistRepository;
import com.tradewise.watchlist.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Default {@link WatchlistService}. Enforces ownership, guarantees a single
 * default watchlist per user, and evaluates price alerts on demand.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistMapper watchlistMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public WatchlistResponse create(UUID userId, WatchlistRequest request) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUserId(userId);
        watchlist.setName(request.name());
        watchlist.setDescription(request.description());
        watchlist.setPinned(Boolean.TRUE.equals(request.pinned()));
        watchlist.setFavorite(Boolean.TRUE.equals(request.favorite()));
        // First watchlist for a user becomes the default automatically.
        watchlist.setDefaultWatchlist(watchlistRepository.findDefaultByUserId(userId).isEmpty());
        stampCreate(watchlist);

        Watchlist saved = watchlistRepository.save(watchlist);
        log.info("Watchlist created id={} user={}", saved.getId(), userId);
        return watchlistMapper.toResponse(saved);
    }

    @Override
    public WatchlistResponse update(UUID userId, UUID watchlistId, WatchlistRequest request) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        watchlist.setName(request.name());
        watchlist.setDescription(request.description());
        if (request.pinned() != null) {
            watchlist.setPinned(request.pinned());
        }
        if (request.favorite() != null) {
            watchlist.setFavorite(request.favorite());
        }
        stampUpdate(watchlist);
        return watchlistMapper.toResponse(watchlistRepository.save(watchlist));
    }

    @Override
    public List<WatchlistResponse> getAll(UUID userId, String search, String sortBy) {
        List<Watchlist> lists = watchlistRepository.findAllByUserId(userId);
        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            lists = lists.stream()
                    .filter(w -> w.getName() != null && w.getName().toLowerCase().contains(q))
                    .toList();
        }
        Comparator<Watchlist> comparator = switch (sortBy == null ? "name" : sortBy) {
            case "createdDate" -> Comparator.comparing(Watchlist::getCreatedDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(Watchlist::getName,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
        // Pinned watchlists surface first, then by the chosen sort.
        return lists.stream()
                .sorted(Comparator.comparing(Watchlist::isPinned).reversed().thenComparing(comparator))
                .map(watchlistMapper::toResponse)
                .toList();
    }

    @Override
    public WatchlistResponse getById(UUID userId, UUID watchlistId) {
        return watchlistMapper.toResponse(getOwned(userId, watchlistId));
    }

    @Override
    public void delete(UUID userId, UUID watchlistId) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        if (watchlist.isDefaultWatchlist()) {
            throw new BusinessRuleException("The default watchlist cannot be deleted");
        }
        watchlist.softDelete(currentUserProvider.getCurrentUsername());
        watchlistRepository.save(watchlist);
        log.info("Watchlist soft-deleted id={}", watchlistId);
    }

    @Override
    public WatchlistResponse togglePin(UUID userId, UUID watchlistId) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        watchlist.setPinned(!watchlist.isPinned());
        stampUpdate(watchlist);
        return watchlistMapper.toResponse(watchlistRepository.save(watchlist));
    }

    @Override
    public WatchlistResponse toggleFavorite(UUID userId, UUID watchlistId) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        watchlist.setFavorite(!watchlist.isFavorite());
        stampUpdate(watchlist);
        return watchlistMapper.toResponse(watchlistRepository.save(watchlist));
    }

    @Override
    public WatchlistResponse addItem(UUID userId, UUID watchlistId, WatchlistItemRequest request) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        WatchlistItem item = new WatchlistItem();
        applyItem(item, request);
        item.setPosition(watchlist.getItems().size());
        watchlist.getItems().add(item);
        stampUpdate(watchlist);
        log.info("Item {} added to watchlist {}", request.stockSymbol(), watchlistId);
        return watchlistMapper.toResponse(watchlistRepository.save(watchlist));
    }

    @Override
    public WatchlistResponse updateItem(UUID userId, UUID watchlistId, UUID itemId, WatchlistItemRequest request) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        WatchlistItem item = findItem(watchlist, itemId);
        applyItem(item, request);
        item.setUpdatedDate(Instant.now());
        stampUpdate(watchlist);
        return watchlistMapper.toResponse(watchlistRepository.save(watchlist));
    }

    @Override
    public WatchlistResponse removeItem(UUID userId, UUID watchlistId, UUID itemId) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        boolean removed = watchlist.getItems().removeIf(i -> i.getId().equals(itemId));
        if (!removed) {
            throw ResourceNotFoundException.of("Watchlist item", itemId);
        }
        reindexPositions(watchlist);
        stampUpdate(watchlist);
        return watchlistMapper.toResponse(watchlistRepository.save(watchlist));
    }

    @Override
    public WatchlistResponse reorderItems(UUID userId, UUID watchlistId, ReorderItemsRequest request) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        List<UUID> order = request.orderedItemIds();
        if (order.size() != watchlist.getItems().size()) {
            throw new BusinessRuleException("Ordered id list must contain every item exactly once");
        }
        for (WatchlistItem item : watchlist.getItems()) {
            int index = order.indexOf(item.getId());
            if (index < 0) {
                throw new BusinessRuleException("Unknown item id in ordering: " + item.getId());
            }
            item.setPosition(index);
        }
        stampUpdate(watchlist);
        return watchlistMapper.toResponse(watchlistRepository.save(watchlist));
    }

    @Override
    public List<AlertResponse> evaluateAlerts(UUID userId, UUID watchlistId, UUID itemId, BigDecimal marketPrice) {
        Watchlist watchlist = getOwned(userId, watchlistId);
        WatchlistItem item = findItem(watchlist, itemId);
        List<AlertResponse> alerts = new ArrayList<>();
        if (!item.isAlertEnabled() || marketPrice == null) {
            return alerts;
        }
        if (item.getTargetPrice() != null && marketPrice.compareTo(item.getTargetPrice()) >= 0) {
            alerts.add(new AlertResponse(item.getId(), item.getStockSymbol(), AlertType.TARGET_HIT,
                    item.getTargetPrice(), marketPrice,
                    "Target price reached for " + item.getStockSymbol()));
        }
        if (item.getStopLoss() != null && marketPrice.compareTo(item.getStopLoss()) <= 0) {
            alerts.add(new AlertResponse(item.getId(), item.getStockSymbol(), AlertType.STOP_LOSS_HIT,
                    item.getStopLoss(), marketPrice,
                    "Stop-loss triggered for " + item.getStockSymbol()));
        }
        return alerts;
    }

    // ----- helpers -----

    private Watchlist getOwned(UUID userId, UUID watchlistId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> ResourceNotFoundException.of("Watchlist", watchlistId));
        if (!watchlist.getUserId().equals(userId)) {
            throw ResourceNotFoundException.of("Watchlist", watchlistId);
        }
        return watchlist;
    }

    private WatchlistItem findItem(Watchlist watchlist, UUID itemId) {
        return watchlist.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.of("Watchlist item", itemId));
    }

    private void applyItem(WatchlistItem item, WatchlistItemRequest request) {
        item.setStockSymbol(request.stockSymbol());
        item.setCompany(request.company());
        item.setExchange(request.exchange());
        item.setSector(request.sector());
        item.setCurrentPrice(request.currentPrice());
        item.setTargetPrice(request.targetPrice());
        item.setStopLoss(request.stopLoss());
        item.setNotes(request.notes());
        item.setPriority(request.priority() != null ? request.priority() : Priority.MEDIUM);
        item.setColorTag(request.colorTag());
        item.setAlertEnabled(Boolean.TRUE.equals(request.alertEnabled()));
    }

    private void reindexPositions(Watchlist watchlist) {
        List<WatchlistItem> ordered = watchlist.getItems().stream()
                .sorted(Comparator.comparingInt(WatchlistItem::getPosition))
                .toList();
        for (int i = 0; i < ordered.size(); i++) {
            ordered.get(i).setPosition(i);
        }
    }

    private void stampCreate(Watchlist watchlist) {
        String actor = currentUserProvider.getCurrentUsername();
        Instant now = Instant.now();
        watchlist.setCreatedBy(actor);
        watchlist.setCreatedDate(now);
        watchlist.setUpdatedBy(actor);
        watchlist.setUpdatedDate(now);
    }

    private void stampUpdate(Watchlist watchlist) {
        watchlist.setUpdatedBy(currentUserProvider.getCurrentUsername());
        watchlist.setUpdatedDate(Instant.now());
    }
}
