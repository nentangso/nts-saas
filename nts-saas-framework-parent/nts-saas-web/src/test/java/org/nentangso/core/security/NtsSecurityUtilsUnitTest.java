package org.nentangso.core.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;

/**
 * Test class for the {@link NtsSecurityUtils} utility class.
 */
class NtsSecurityUtilsUnitTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        NtsSecurityUtils securityUtils = new NtsSecurityUtils("roles", "ROLE_", true);
        securityUtils.afterPropertiesSet();
    }

    @Test
    void testGetCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        Optional<String> login = NtsSecurityUtils.getCurrentUserLogin();
        assertThat(login).contains("admin");
    }

    @Test
    void testGetCurrentUserLoginForOAuth2() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", NtsAuthoritiesConstants.USER);
        claims.put("sub", 123);
        claims.put("preferred_username", "admin");
        OidcIdToken idToken = new OidcIdToken(ID_TOKEN, Instant.now(), Instant.now().plusSeconds(60), claims);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER));
        OidcUser user = new DefaultOidcUser(authorities, idToken);
        OAuth2AuthenticationToken auth2AuthenticationToken = new OAuth2AuthenticationToken(user, authorities, "oidc");
        securityContext.setAuthentication(auth2AuthenticationToken);
        SecurityContextHolder.setContext(securityContext);

        Optional<String> login = NtsSecurityUtils.getCurrentUserLogin();

        assertThat(login).contains("admin");
    }

    @Test
    void testExtractAuthorityFromClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", Arrays.asList(NtsAuthoritiesConstants.ADMIN, NtsAuthoritiesConstants.USER));

        List<GrantedAuthority> expectedAuthorities = Arrays.asList(
            new SimpleGrantedAuthority(NtsAuthoritiesConstants.ADMIN),
            new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER)
        );

        List<GrantedAuthority> authorities = NtsSecurityUtils.extractAuthorityFromClaims(claims);

        assertThat(authorities).isNotNull().isNotEmpty().hasSize(2).containsAll(expectedAuthorities);
    }

    @Test
    void testExtractAuthorityFromClaims_NamespacedRoles() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", Arrays.asList(NtsAuthoritiesConstants.ADMIN, NtsAuthoritiesConstants.USER));

        List<GrantedAuthority> expectedAuthorities = Arrays.asList(
            new SimpleGrantedAuthority(NtsAuthoritiesConstants.ADMIN),
            new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER)
        );

        List<GrantedAuthority> authorities = NtsSecurityUtils.extractAuthorityFromClaims(claims);

        assertThat(authorities).isNotNull().isNotEmpty().hasSize(2).containsAll(expectedAuthorities);
    }

    @Test
    void testIsAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = NtsSecurityUtils.isAuthenticated();
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    void testAnonymousIsNotAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.ANONYMOUS));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = NtsSecurityUtils.isAuthenticated();
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    void testHasCurrentUserThisAuthority() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        assertThat(NtsSecurityUtils.hasCurrentUserThisAuthority(NtsAuthoritiesConstants.USER)).isTrue();
        assertThat(NtsSecurityUtils.hasCurrentUserThisAuthority(NtsAuthoritiesConstants.ADMIN)).isFalse();
    }

    @Test
    void testHasCurrentUserAnyOfAuthorities() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        assertThat(NtsSecurityUtils.hasCurrentUserAnyOfAuthorities(NtsAuthoritiesConstants.USER, NtsAuthoritiesConstants.ADMIN)).isTrue();
        assertThat(NtsSecurityUtils.hasCurrentUserAnyOfAuthorities(NtsAuthoritiesConstants.ANONYMOUS, NtsAuthoritiesConstants.ADMIN)).isFalse();
    }

    @Test
    void testHasCurrentUserNoneOfAuthorities() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        assertThat(NtsSecurityUtils.hasCurrentUserNoneOfAuthorities(NtsAuthoritiesConstants.USER, NtsAuthoritiesConstants.ADMIN)).isFalse();
        assertThat(NtsSecurityUtils.hasCurrentUserNoneOfAuthorities(NtsAuthoritiesConstants.ANONYMOUS, NtsAuthoritiesConstants.ADMIN)).isTrue();
    }

    @Test
    void testDisplayName() {
        assertThat(NtsSecurityUtils.toDisplayName("Foo ", "Bar")).isEqualTo("Bar Foo");
        assertThat(NtsSecurityUtils.toDisplayName(" Foz ", " Baz")).isEqualTo("Baz Foz");
        assertThat(NtsSecurityUtils.toDisplayName(null, null)).isNull();
    }
}
