package com.tradewise.auth.persistence;

import com.tradewise.auth.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "version", ignore = true)
    UserJpaEntity toEntity(User user);

    User toDomain(UserJpaEntity entity);

    @Mapping(target = "version", ignore = true)
    void updateEntity(User user, @MappingTarget UserJpaEntity entity);
}