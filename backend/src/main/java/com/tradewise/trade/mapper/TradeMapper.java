package com.tradewise.trade.mapper;

import com.tradewise.trade.domain.Trade;
import com.tradewise.trade.dto.request.TradeRequest;
import com.tradewise.trade.dto.response.TradeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Maps {@link Trade} entities to/from DTOs. Derived and ownership fields are
 * managed by the service, not the mapper.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TradeMapper {

    TradeResponse toResponse(Trade trade);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "profitLoss", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Trade toEntity(TradeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "profitLoss", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(TradeRequest request, @MappingTarget Trade trade);
}
