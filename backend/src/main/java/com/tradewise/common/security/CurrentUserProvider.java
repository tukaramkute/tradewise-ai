package com.tradewise.common.security;

import org.springframework.stereotype.Component;

/**
 * Supplies the identity of the currently acting principal for audit fields.
 *
 * <p>Phase 1 has no authenticated security context, so this returns a system
 * actor. In Phase 3 the JWT-based implementation reads the authenticated user
 * from the {@code SecurityContextHolder}. Business code depends on this
 * abstraction only, so the switch is transparent.</p>
 */
@Component
public class CurrentUserProvider {

    public static final String SYSTEM_ACTOR = "system";

    public String getCurrentUsername() {
        return SYSTEM_ACTOR;
    }
}
