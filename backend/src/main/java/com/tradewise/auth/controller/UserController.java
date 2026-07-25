package com.tradewise.auth.controller;

import com.tradewise.auth.dto.request.ChangePasswordRequest;
import com.tradewise.auth.dto.request.UpdateProfileRequest;
import com.tradewise.auth.dto.response.UserResponse;
import com.tradewise.auth.service.UserService;
import com.tradewise.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Authenticated user/profile endpoints.
 *
 * <p>Phase 1 has no security context, so the acting user is supplied via the
 * {@code X-User-Id} header. In Phase 3 this is replaced by the JWT principal
 * (e.g. {@code @AuthenticationPrincipal}) and the header is removed.</p>
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Current user, profile update, password and account management")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> currentUser(
            @Parameter(description = "Acting user id (temporary; replaced by JWT in Phase 3)")
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(userId)));
    }

    @Operation(summary = "Update the current user's profile (email cannot be changed)")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.updateProfile(userId, request), "Profile updated"));
    }

    @Operation(summary = "Change the current user's password")
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @Operation(summary = "Soft-delete the current user account")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @RequestHeader("X-User-Id") UUID userId) {
        userService.softDelete(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Account deleted"));
    }
}
