package org.nentangso.core.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;

import java.util.Optional;

public class NtsOAuth2ClientCredentialsRequestInterceptor implements RequestInterceptor {
    // Using anonymous user principal as its S2S authentication
    public static final Authentication ANONYMOUS_USER_AUTHENTICATION = new AnonymousAuthenticationToken(
        "key",
        "anonymous",
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
    );

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final String clientRegistrationId;

    public NtsOAuth2ClientCredentialsRequestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager, String clientRegistrationId) {
        this.authorizedClientManager = authorizedClientManager;
        this.clientRegistrationId = clientRegistrationId;
    }

    @Override
    public void apply(RequestTemplate template) {
        Optional<String> authorizationToken = getAuthenticationToken(clientRegistrationId);
        authorizationToken.ifPresent(s -> template.header(HttpHeaders.AUTHORIZATION, s));
    }

    public Optional<String> getAuthenticationToken(final String clientRegistrationId) {
        final OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                .principal(ANONYMOUS_USER_AUTHENTICATION)
                .build();
        OAuth2AuthorizedClient authorize = authorizedClientManager.authorize(request);
        return Optional.ofNullable(authorize)
            .map(OAuth2AuthorizedClient::getAccessToken)
            .map(AbstractOAuth2Token::getTokenValue)
            .map(accessToken -> "Bearer " + accessToken);
    }
}
