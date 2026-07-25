package com.tradewise.auth.service;

import com.tradewise.auth.domain.User;
import com.tradewise.auth.dto.request.ChangePasswordRequest;
import com.tradewise.auth.dto.request.UpdateProfileRequest;
import com.tradewise.auth.dto.response.UserResponse;

import java.util.UUID;

/**
 * User profile / account use cases for an authenticated user.
 */
public interface UserService {

    UserResponse getById(UUID userId);

    UserResponse updateProfile(UUID userId, UpdateProfileRequest request);

    void changePassword(UUID userId, ChangePasswordRequest request);

    void softDelete(UUID userId);

    /** Internal helper for other modules that need the entity. */
    User getEntityById(UUID userId);
}
