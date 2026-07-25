package com.tradewise.watchlist.repository;

import com.tradewise.common.repository.CrudRepository;
import com.tradewise.watchlist.domain.Watchlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WatchlistRepository extends CrudRepository<Watchlist> {

    List<Watchlist> findAllByUserId(UUID userId);

    Optional<Watchlist> findDefaultByUserId(UUID userId);
}
