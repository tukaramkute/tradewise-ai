package com.tradewise.auth.service.impl;

import com.tradewise.auth.domain.User;
import com.tradewise.auth.domain.enums.Role;
import com.tradewise.auth.domain.enums.UserStatus;
import com.tradewise.auth.dto.request.ForgotPasswordRequest;
import com.tradewise.auth.dto.request.LoginRequest;
import com.tradewise.auth.dto.request.LogoutRequest;
import com.tradewise.auth.dto.request.RefreshTokenRequest;
import com.tradewise.auth.dto.request.RegisterRequest;
import com.tradewise.auth.dto.request.ResetPasswordRequest;
import com.tradewise.auth.dto.response.AuthResponse;
import com.tradewise.auth.dto.response.OtpResponse;
import com.tradewise.auth.dto.response.TokenResponse;
import com.tradewise.auth.dto.response.UserResponse;
import com.tradewise.auth.mapper.UserMapper;
import com.tradewise.auth.otp.OtpStore;
import com.tradewise.auth.repository.UserRepository;
import com.tradewise.auth.service.AuthService;
import com.tradewise.auth.token.RefreshTokenStore;
import com.tradewise.auth.token.TokenService;
import com.tradewise.common.exception.BusinessRuleException;
import com.tradewise.common.exception.DuplicateResourceException;
import com.tradewise.common.exception.ResourceNotFoundException;
import com.tradewise.common.exception.UnauthorizedException;
import com.tradewise.common.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Default {@link AuthService} implementation. Orchestrates validation of
 * uniqueness rules, secure password hashing, token issuance and the OTP-based
 * password-reset flow. Contains no persistence details — those live behind the
 * repository and store abstractions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenStore refreshTokenStore;
    private final OtpStore otpStore;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user username={} email={}", request.getUsername(), request.getEmail());
        assertUnique(request);

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setRoles(EnumSet.of(Role.ROLE_USER));
        user.setEmailVerified(false);

        String actor = currentUserProvider.getCurrentUsername();
        Instant now = Instant.now();
        user.setCreatedBy(actor);
        user.setCreatedDate(now);
        user.setUpdatedBy(actor);
        user.setUpdatedDate(now);

        User saved = userRepository.save(user);
        log.info("User registered id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.identifier())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Failed login attempt for identifier={}", request.identifier());
            throw new UnauthorizedException("Invalid credentials");
        }
        if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.DEACTIVATED) {
            throw new BusinessRuleException("Account is " + user.getStatus().name().toLowerCase());
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        refreshTokenStore.store(refreshToken, user.getId());

        log.info("User logged in id={}", user.getId());
        return AuthResponse.of(accessToken, refreshToken,
                tokenService.getAccessTokenExpiresInSeconds(), userMapper.toResponse(user));
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        UUID userId = refreshTokenStore.resolveActiveUser(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid or expired"));

        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new UnauthorizedException("Account no longer exists"));

        // Rotate refresh token: invalidate the old, issue a new pair.
        refreshTokenStore.invalidate(request.refreshToken());
        String newAccess = tokenService.generateAccessToken(user);
        String newRefresh = tokenService.generateRefreshToken(user);
        refreshTokenStore.store(newRefresh, user.getId());

        log.debug("Refreshed tokens for user id={}", user.getId());
        return TokenResponse.of(newAccess, newRefresh, tokenService.getAccessTokenExpiresInSeconds());
    }

    @Override
    public void logout(LogoutRequest request) {
        refreshTokenStore.invalidate(request.refreshToken());
        log.info("Refresh token invalidated on logout");
    }

    @Override
    public OtpResponse forgotPassword(ForgotPasswordRequest request) {
        // Do not reveal whether the email exists (avoid user enumeration).
        var userOpt = userRepository.findByEmailIgnoreCase(request.email());
        if (userOpt.isEmpty()) {
            log.info("Forgot-password requested for non-existent email (no OTP issued)");
            return new OtpResponse(
                    "If an account exists for this email, an OTP has been sent.", null, null);
        }
        OtpStore.GeneratedOtp otp = otpStore.generate(request.email());
        log.info("OTP generated for user id={}", userOpt.get().getId());
        // Phase 1: OTP echoed back as devOtp; Phase 3 delivers it by email only.
        return new OtpResponse(
                "If an account exists for this email, an OTP has been sent.",
                otp.expiresAt(), otp.code());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found for this email"));

        if (!otpStore.verifyAndConsume(request.getEmail(), request.getOtp())) {
            throw new BusinessRuleException("OTP is invalid or expired", "INVALID_OTP");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        touch(user);
        userRepository.save(user);

        // Force re-authentication everywhere after a password reset.
        refreshTokenStore.invalidateAllForUser(user.getId());
        log.info("Password reset for user id={}", user.getId());
    }

    // ----- helpers -----

    private void assertUnique(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw DuplicateResourceException.of("Username", request.getUsername());
        }
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw DuplicateResourceException.of("Email", request.getEmail());
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw DuplicateResourceException.of("Phone number", request.getPhoneNumber());
        }
    }

    private void touch(User user) {
        user.setUpdatedBy(currentUserProvider.getCurrentUsername());
        user.setUpdatedDate(Instant.now());
    }
}
