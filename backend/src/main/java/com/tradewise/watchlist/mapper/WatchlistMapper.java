package com.tradewise.watchlist.mapper;

import com.tradewise.watchlist.domain.Watchlist;
import com.tradewise.watchlist.domain.WatchlistItem;
import com.tradewise.watchlist.dto.response.WatchlistItemResponse;
import com.tradewise.watchlist.dto.response.WatchlistResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

/**
 * Maps watchlist aggregates and items to their response DTOs. Items are always
 * returned ordered by their drag-and-drop {@code position}.
 */
@Mapper(componentModel = "spring")
public interface WatchlistMapper {

    WatchlistItemResponse toItemResponse(WatchlistItem item);

    @Mapping(target = "itemCount", expression = "java(watchlist.getItems().size())")
    @Mapping(target = "items", expression = "java(toOrderedItems(watchlist))")
    WatchlistResponse toResponse(Watchlist watchlist);

    default List<WatchlistItemResponse> toOrderedItems(Watchlist watchlist) {
        return watchlist.getItems().stream()
                .sorted(Comparator.comparingInt(WatchlistItem::getPosition))
                .map(this::toItemResponse)
                .toList();
    }
}
