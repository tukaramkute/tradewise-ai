package com.tradewise.auth.mapper;

import com.tradewise.auth.domain.User;
import com.tradewise.auth.dto.request.RegisterRequest;
import com.tradewise.auth.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Maps between the {@link User} entity and its DTOs. The entity is never exposed
 * directly to controllers.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    UserResponse toResponse(User user);

    /**
     * Builds a new {@link User} from a registration request. Sensitive/derived
     * fields (password hash, id, status, roles, audit) are set by the service.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    User toEntity(RegisterRequest request);
}
