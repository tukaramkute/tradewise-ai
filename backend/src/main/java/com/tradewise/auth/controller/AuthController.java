package com.tradewise.auth.controller;

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
import com.tradewise.auth.service.AuthService;
import com.tradewise.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public authentication endpoints: registration, login, token refresh, logout
 * and the forgot/reset-password flow.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login and password management")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "Registration successful"));
    }

    @Operation(summary = "Login with username or email")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful"));
    }

    @Operation(summary = "Obtain a new access token from a refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(request), "Token refreshed"));
    }

    @Operation(summary = "Logout and invalidate the refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @Operation(summary = "Request a password-reset OTP")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<OtpResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.forgotPassword(request), "OTP processed"));
    }

    @Operation(summary = "Reset password using an OTP")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successful"));
    }
}
