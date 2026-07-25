package com.tradewise.auth.otp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory One-Time-Password store for the forgot-password flow. Keyed by the
 * lower-cased email. Handles generation, expiry and single-use consumption.
 */
@Component
public class OtpStore {

    private final SecureRandom random = new SecureRandom();
    private final int otpLength;
    private final long expirationMinutes;

    private record Otp(String code, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    private final ConcurrentHashMap<String, Otp> otps = new ConcurrentHashMap<>();

    public OtpStore(@Value("${tradewise.otp.length}") int otpLength,
                    @Value("${tradewise.otp.expiration-minutes}") long expirationMinutes) {
        this.otpLength = otpLength;
        this.expirationMinutes = expirationMinutes;
    }

    /** Generates, stores and returns a fresh OTP for the given key. */
    public GeneratedOtp generate(String key) {
        String code = randomDigits(otpLength);
        Instant expiresAt = Instant.now().plusSeconds(expirationMinutes * 60);
        otps.put(normalize(key), new Otp(code, expiresAt));
        return new GeneratedOtp(code, expiresAt);
    }

    /**
     * Validates the supplied code for the key. On success the OTP is consumed
     * (single use) and {@code true} is returned.
     */
    public boolean verifyAndConsume(String key, String code) {
        String normalizedKey = normalize(key);
        Otp otp = otps.get(normalizedKey);
        if (otp == null || otp.isExpired()) {
            otps.remove(normalizedKey);
            return false;
        }
        if (!otp.code().equals(code)) {
            return false;
        }
        otps.remove(normalizedKey);
        return true;
    }

    public Optional<Instant> peekExpiry(String key) {
        return Optional.ofNullable(otps.get(normalize(key))).map(Otp::expiresAt);
    }

    private String randomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String normalize(String key) {
        return key == null ? "" : key.trim().toLowerCase();
    }

    public record GeneratedOtp(String code, Instant expiresAt) {
    }
}
