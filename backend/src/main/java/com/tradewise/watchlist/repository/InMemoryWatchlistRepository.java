package com.tradewise.watchlist.repository;

import com.tradewise.common.repository.InMemoryCrudRepository;
import com.tradewise.watchlist.domain.Watchlist;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryWatchlistRepository extends InMemoryCrudRepository<Watchlist> implements WatchlistRepository {

    @Override
    public List<Watchlist> findAllByUserId(UUID userId) {
        return findActiveBy(w -> userId.equals(w.getUserId()));
    }

    @Override
    public Optional<Watchlist> findDefaultByUserId(UUID userId) {
        return findFirstActiveBy(w -> userId.equals(w.getUserId()) && w.isDefaultWatchlist());
    }
}
