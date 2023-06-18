package org.nentangso.core.security;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.config.NtsProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security.
 */
@Component
public final class NtsSecurityUtils implements InitializingBean {
    private final String rolesClaim;
    private final String rolePrefix;
    private final boolean reverseOrderOfDisplayName;

    @Autowired
    public NtsSecurityUtils(NtsProperties ntsProperties) {
        this.rolesClaim = ntsProperties.getSecurity().getOauth2().getRolesClaim();
        this.rolePrefix = ntsProperties.getSecurity().getOauth2().getRolePrefix();
        this.reverseOrderOfDisplayName = ntsProperties.getSecurity().getOauth2().isReverseOrderOfDisplayName();
    }

    public NtsSecurityUtils(String rolesClaim, String rolePrefix, boolean reverseOrderOfDisplayName) {
        this.rolesClaim = rolesClaim;
        this.rolePrefix = rolePrefix;
        this.reverseOrderOfDisplayName = reverseOrderOfDisplayName;
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
        } else if (authentication instanceof JwtAuthenticationToken) {
            return (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get("preferred_username");
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
            if (attributes.containsKey("preferred_username")) {
                return (String) attributes.get("preferred_username");
            }
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get the display name of the current user.
     *
     * @return the display name of the current user.
     */
    public static Optional<String> getCurrentUserDisplayName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractDisplayName(securityContext.getAuthentication()));
    }

    private static String extractDisplayName(Authentication authentication) {
        String firstName = null;
        String lastName = null;
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            firstName = springSecurityUser.getUsername();
        } else if (authentication instanceof JwtAuthenticationToken) {
            firstName = (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get("given_name");
            lastName = (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get("family_name");
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
            if (attributes.containsKey("given_name")) {
                firstName = (String) attributes.get("given_name");
            }
            if (attributes.containsKey("family_name")) {
                lastName = (String) attributes.get("family_name");
            }
        } else if (authentication.getPrincipal() instanceof String) {
            firstName = (String) authentication.getPrincipal();
        }
        return toDisplayName(firstName, lastName);
    }

    public static String toDisplayName(String firstName, String lastName) {
        List<String> names = Arrays.asList(firstName, lastName);
        if (instance.reverseOrderOfDisplayName) {
            Collections.reverse(names);
        }
        String displayName = names.stream()
            .map(StringUtils::trimToNull)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));
        return StringUtils.trimToNull(displayName);
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).noneMatch(NtsAuthoritiesConstants.ANONYMOUS::equals);
    }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
    public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (
            authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
        );
    }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false otherwise.
     */
    public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
        return !hasCurrentUserAnyOfAuthorities(authorities);
    }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    public static boolean hasCurrentUserThisAuthority(String authority) {
        return hasCurrentUserAnyOfAuthorities(authority);
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication instanceof JwtAuthenticationToken
            ? extractAuthorityFromClaims(((JwtAuthenticationToken) authentication).getToken().getClaims())
            : authentication.getAuthorities();
        return authorities.stream().map(GrantedAuthority::getAuthority);
    }

    public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
        return mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
    }

    @SuppressWarnings("unchecked")
    private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
        return (Collection<String>) claims.getOrDefault(
            instance.rolesClaim,
            new ArrayList<>()
        );
    }

    private static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
        return roles.stream()
            .filter(role -> role.startsWith(instance.rolePrefix))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    /**
     * Get claim value of current user by claim name
     *
     * @param claim the name of claim in JWT
     * @return Optional of claim value
     */
    public static Optional<Object> getCurrentUserClaim(String claim) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return extractClaim(authentication, claim);
    }

    private static Optional<Object> extractClaim(Authentication authentication, String claim) {
        if (authentication == null) {
            return Optional.empty();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return Optional.empty();
        } else if (authentication instanceof JwtAuthenticationToken) {
            Object value = ((JwtAuthenticationToken) authentication).getToken().getClaims().get(claim);
            return Optional.ofNullable(value);
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
            if (attributes.containsKey(claim)) {
                Object value = attributes.get(claim);
                return Optional.ofNullable(value);
            }
        } else if (authentication.getPrincipal() instanceof String) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static NtsSecurityUtils instance;

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }
}
