package com.tradewise.auth.token;

import com.tradewise.auth.domain.User;

/**
 * Issues and validates authentication tokens.
 *
 * <p>Phase 1 provides an opaque, in-memory implementation. Phase 3 swaps in a
 * signed-JWT implementation without changing the service layer, because callers
 * depend only on this interface.</p>
 */
public interface TokenService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    /** Access-token lifetime in seconds (for the {@code expiresIn} field). */
    long getAccessTokenExpiresInSeconds();
}
