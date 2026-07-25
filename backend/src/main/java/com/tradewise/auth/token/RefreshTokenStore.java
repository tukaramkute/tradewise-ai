package com.tradewise.auth.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of issued refresh tokens. Enables logout (invalidation)
 * and refresh validation. Backed by a {@code ConcurrentHashMap}; a persistent
 * token store can replace it later behind the same method surface.
 */
@Component
public class RefreshTokenStore {

    private final long refreshTokenExpirationMs;

    private record StoredToken(UUID userId, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    private final ConcurrentHashMap<String, StoredToken> tokens = new ConcurrentHashMap<>();

    public RefreshTokenStore(
            @Value("${tradewise.security.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public void store(String refreshToken, UUID userId) {
        tokens.put(refreshToken, new StoredToken(userId,
                Instant.now().plusMillis(refreshTokenExpirationMs)));
    }

    /** Returns the owning user id if the token exists and is not expired. */
    public Optional<UUID> resolveActiveUser(String refreshToken) {
        StoredToken stored = tokens.get(refreshToken);
        if (stored == null) {
            return Optional.empty();
        }
        if (stored.isExpired()) {
            tokens.remove(refreshToken);
            return Optional.empty();
        }
        return Optional.of(stored.userId());
    }

    public void invalidate(String refreshToken) {
        tokens.remove(refreshToken);
    }

    public void invalidateAllForUser(UUID userId) {
        tokens.entrySet().removeIf(e -> e.getValue().userId().equals(userId));
    }
}
