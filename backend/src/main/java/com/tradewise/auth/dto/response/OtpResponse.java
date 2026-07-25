package com.tradewise.auth.dto.response;

import java.time.Instant;

/**
 * Response for the forgot-password flow. In Phase 1 (no email delivery) the OTP
 * is echoed back to support local testing; from Phase 3 it will be delivered by
 * email only and this field will be omitted.
 */
public record OtpResponse(
        String message,
        Instant expiresAt,
        String devOtp
) {
}
