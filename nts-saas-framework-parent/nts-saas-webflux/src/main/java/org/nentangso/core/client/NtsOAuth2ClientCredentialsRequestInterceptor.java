package org.nentangso.core.client;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactivefeign.utils.MultiValueMapUtils;
import reactor.core.publisher.Mono;

public class NtsOAuth2ClientCredentialsRequestInterceptor implements ReactiveHttpRequestInterceptor {
    // Using anonymous user principal as its S2S authentication
    public static final Authentication ANONYMOUS_USER_AUTHENTICATION = new AnonymousAuthenticationToken(
        "key",
        "anonymous",
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
    );

    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;
    private final String clientRegistrationId;

    public NtsOAuth2ClientCredentialsRequestInterceptor(ReactiveOAuth2AuthorizedClientManager authorizedClientManager, String clientRegistrationId) {
        this.authorizedClientManager = authorizedClientManager;
        this.clientRegistrationId = clientRegistrationId;
    }

    @Override
    public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest request) {
        return getAuthenticationToken(clientRegistrationId)
            .map(accessToken -> {
                MultiValueMapUtils.addOrdered(request.headers(), HttpHeaders.AUTHORIZATION, accessToken);
                return request;
            });
    }

    public Mono<String> getAuthenticationToken(final String clientRegistrationId) {
        final OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
            .principal(ANONYMOUS_USER_AUTHENTICATION)
            .build();
        return authorizedClientManager.authorize(request)
            .map(OAuth2AuthorizedClient::getAccessToken)
            .map(AbstractOAuth2Token::getTokenValue)
            .map(accessToken -> "Bearer " + accessToken);
    }
}
