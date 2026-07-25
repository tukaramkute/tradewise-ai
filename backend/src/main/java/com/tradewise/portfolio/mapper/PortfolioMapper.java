package com.tradewise.portfolio.mapper;

import com.tradewise.portfolio.domain.Portfolio;
import com.tradewise.portfolio.dto.response.PortfolioResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {

    PortfolioResponse toResponse(Portfolio portfolio);
}
