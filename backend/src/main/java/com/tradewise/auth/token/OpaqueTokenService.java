package com.tradewise.auth.token;

import com.tradewise.auth.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

/**
 * Phase 1 opaque-token implementation. Produces random, non-guessable tokens so
 * the full auth flow works end-to-end before JWT is introduced in Phase 3.
 */
@Service
public class OpaqueTokenService implements TokenService {

    private final long accessTokenExpirationMs;

    public OpaqueTokenService(
            @Value("${tradewise.security.jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    @Override
    public String generateAccessToken(User user) {
        return "act_" + encode(user.getId());
    }

    @Override
    public String generateRefreshToken(User user) {
        return "rft_" + encode(user.getId());
    }

    @Override
    public long getAccessTokenExpiresInSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    private String encode(UUID userId) {
        String raw = userId + ":" + UUID.randomUUID();
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes());
    }
}
