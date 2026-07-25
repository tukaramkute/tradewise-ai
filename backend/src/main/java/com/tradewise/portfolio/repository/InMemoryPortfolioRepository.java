package com.tradewise.portfolio.repository;

import com.tradewise.common.repository.InMemoryCrudRepository;
import com.tradewise.portfolio.domain.Portfolio;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryPortfolioRepository extends InMemoryCrudRepository<Portfolio>
        implements PortfolioRepository {

    @Override
    public Optional<Portfolio> findByUserId(UUID userId) {
        return findFirstActiveBy(p -> userId.equals(p.getUserId()));
    }
}
