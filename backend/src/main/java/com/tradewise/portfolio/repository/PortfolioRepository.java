package com.tradewise.portfolio.repository;

import com.tradewise.common.repository.CrudRepository;
import com.tradewise.portfolio.domain.Portfolio;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends CrudRepository<Portfolio> {

    Optional<Portfolio> findByUserId(UUID userId);
}
