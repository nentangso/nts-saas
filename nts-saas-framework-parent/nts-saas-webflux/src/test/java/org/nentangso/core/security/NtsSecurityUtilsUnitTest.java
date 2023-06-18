package org.nentangso.core.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

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
    void testgetCurrentUserLogin() {
        String login = NtsSecurityUtils
            .getCurrentUserLogin()
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin")))
            .block();
        assertThat(login).isEqualTo("admin");
    }

    @Test
    void testIsAuthenticated() {
        Boolean isAuthenticated = NtsSecurityUtils
            .isAuthenticated()
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin")))
            .block();
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    void testAnonymousIsNotAuthenticated() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.ANONYMOUS));
        Boolean isAuthenticated = NtsSecurityUtils
            .isAuthenticated()
            .contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", authorities))
            )
            .block();
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    void testHasCurrentUserAnyOfAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER));
        Context context = ReactiveSecurityContextHolder.withAuthentication(
            new UsernamePasswordAuthenticationToken("admin", "admin", authorities)
        );
        Boolean hasCurrentUserThisAuthority = NtsSecurityUtils
            .hasCurrentUserAnyOfAuthorities(NtsAuthoritiesConstants.USER, NtsAuthoritiesConstants.ADMIN)
            .contextWrite(context)
            .block();
        assertThat(hasCurrentUserThisAuthority).isTrue();

        hasCurrentUserThisAuthority =
            NtsSecurityUtils
                .hasCurrentUserAnyOfAuthorities(NtsAuthoritiesConstants.ANONYMOUS, NtsAuthoritiesConstants.ADMIN)
                .contextWrite(context)
                .block();
        assertThat(hasCurrentUserThisAuthority).isFalse();
    }

    @Test
    void testHasCurrentUserNoneOfAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER));
        Context context = ReactiveSecurityContextHolder.withAuthentication(
            new UsernamePasswordAuthenticationToken("admin", "admin", authorities)
        );
        Boolean hasCurrentUserThisAuthority = NtsSecurityUtils
            .hasCurrentUserNoneOfAuthorities(NtsAuthoritiesConstants.USER, NtsAuthoritiesConstants.ADMIN)
            .contextWrite(context)
            .block();
        assertThat(hasCurrentUserThisAuthority).isFalse();

        hasCurrentUserThisAuthority =
            NtsSecurityUtils
                .hasCurrentUserNoneOfAuthorities(NtsAuthoritiesConstants.ANONYMOUS, NtsAuthoritiesConstants.ADMIN)
                .contextWrite(context)
                .block();
        assertThat(hasCurrentUserThisAuthority).isTrue();
    }

    @Test
    void testHasCurrentUserThisAuthority() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(NtsAuthoritiesConstants.USER));
        Context context = ReactiveSecurityContextHolder.withAuthentication(
            new UsernamePasswordAuthenticationToken("admin", "admin", authorities)
        );
        Boolean hasCurrentUserThisAuthority = NtsSecurityUtils
            .hasCurrentUserThisAuthority(NtsAuthoritiesConstants.USER)
            .contextWrite(context)
            .block();
        assertThat(hasCurrentUserThisAuthority).isTrue();

        hasCurrentUserThisAuthority = NtsSecurityUtils.hasCurrentUserThisAuthority(NtsAuthoritiesConstants.ADMIN).contextWrite(context).block();
        assertThat(hasCurrentUserThisAuthority).isFalse();
    }

    @Test
    void testDisplayName() {
        assertThat(NtsSecurityUtils.toDisplayName("Foo ", "Bar")).isEqualTo("Bar Foo");
        assertThat(NtsSecurityUtils.toDisplayName(" Foz ", " Baz")).isEqualTo("Baz Foz");
        assertThat(NtsSecurityUtils.toDisplayName(null, null)).isNull();
    }
}
