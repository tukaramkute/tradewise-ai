package com.tradewise.auth.service;

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

/**
 * Authentication use cases. Defined as an interface to keep controllers
 * decoupled from the implementation (SOLID / DIP).
 */
public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    TokenResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);

    OtpResponse forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
