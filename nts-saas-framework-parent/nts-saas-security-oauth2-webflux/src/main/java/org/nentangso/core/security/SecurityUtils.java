package org.nentangso.core.security;

import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Mono<String> getCurrentUserLogin() {
        return NtsSecurityHelper.getInstance().getCurrentUserLogin();
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public static Mono<Boolean> isAuthenticated() {
        return NtsSecurityHelper.getInstance().isAuthenticated();
    }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
    public static Mono<Boolean> hasCurrentUserAnyOfAuthorities(String... authorities) {
        return NtsSecurityHelper.getInstance().hasCurrentUserAnyOfAuthorities(authorities);
    }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false otherwise.
     */
    public static Mono<Boolean> hasCurrentUserNoneOfAuthorities(String... authorities) {
        return NtsSecurityHelper.getInstance().hasCurrentUserNoneOfAuthorities(authorities);
    }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    public static Mono<Boolean> hasCurrentUserThisAuthority(String authority) {
        return NtsSecurityHelper.getInstance().hasCurrentUserThisAuthority(authority);
    }

    public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
        return NtsSecurityHelper.getInstance().extractAuthorityFromClaims(claims);
    }
}
