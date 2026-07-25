package com.tradewise.auth.service.impl;

import com.tradewise.auth.domain.User;
import com.tradewise.auth.dto.request.ChangePasswordRequest;
import com.tradewise.auth.dto.request.UpdateProfileRequest;
import com.tradewise.auth.dto.response.UserResponse;
import com.tradewise.auth.mapper.UserMapper;
import com.tradewise.auth.repository.UserRepository;
import com.tradewise.auth.service.UserService;
import com.tradewise.auth.token.RefreshTokenStore;
import com.tradewise.common.exception.BusinessRuleException;
import com.tradewise.common.exception.ResourceNotFoundException;
import com.tradewise.common.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Default {@link UserService}. Handles profile reads/updates, password change
 * and soft deletion. Email is never mutated here — it cannot be changed
 * directly per the specification.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenStore refreshTokenStore;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID userId) {
        return userMapper.toResponse(getEntityById(userId));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = getEntityById(userId);

        applyIfPresent(request.firstName(), user::setFirstName);
        applyIfPresent(request.lastName(), user::setLastName);
        applyIfPresent(request.phoneNumber(), user::setPhoneNumber);
        applyIfPresent(request.profileImageUrl(), user::setProfileImageUrl);
        applyIfPresent(request.timeZone(), user::setTimeZone);
        applyIfPresent(request.country(), user::setCountry);
        applyIfPresent(request.preferredCurrency(), user::setPreferredCurrency);

        touch(user);
        User saved = userRepository.save(user);
        log.info("Profile updated for user id={}", userId);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = getEntityById(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("Current password is incorrect", "INVALID_OLD_PASSWORD");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("New password must differ from the current password");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        touch(user);
        userRepository.save(user);
        refreshTokenStore.invalidateAllForUser(userId);
        log.info("Password changed for user id={}", userId);
    }

    @Override
    @Transactional
    public void softDelete(UUID userId) {
        User user = getEntityById(userId);
        user.softDelete(currentUserProvider.getCurrentUsername());
        userRepository.save(user);
        refreshTokenStore.invalidateAllForUser(userId);
        log.info("User soft-deleted id={}", userId);
    }

    @Override
    public User getEntityById(UUID userId) {
        return userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
    }

    private void applyIfPresent(String value, java.util.function.Consumer<String> setter) {
        if (value != null && !value.isBlank()) {
            setter.accept(value);
        }
    }

    private void touch(User user) {
        user.setUpdatedBy(currentUserProvider.getCurrentUsername());
        user.setUpdatedDate(Instant.now());
    }
}
