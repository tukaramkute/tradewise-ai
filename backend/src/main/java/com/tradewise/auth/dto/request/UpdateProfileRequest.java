package com.tradewise.auth.dto.request;

import com.tradewise.common.validation.ValidationPatterns;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Profile update. Email is intentionally excluded — it cannot be changed
 * directly. Fields are optional; only non-null values are applied.
 */
public record UpdateProfileRequest(
        @Size(max = 50) String firstName,
        @Size(max = 50) String lastName,

        @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
        String phoneNumber,

        String profileImageUrl,
        String timeZone,
        String country,

        @Size(min = 3, max = 3, message = "Preferred currency must be a 3-letter ISO code")
        String preferredCurrency
) {
}
