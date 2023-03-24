package org.nentangso.core.security.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class AuthorizationHeaderUtil {

    private final ReactiveOAuth2AuthorizedClientService clientService;
    private final WebClient webClient = WebClient.create();
    private final Logger log = LoggerFactory.getLogger(AuthorizationHeaderUtil.class);

    public AuthorizationHeaderUtil(ReactiveOAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    public Mono<String> getAuthorizationHeader() {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .flatMap(authentication -> {
                if (authentication instanceof OAuth2AuthenticationToken) {
                    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                    String name = oauthToken.getName();
                    String registrationId = oauthToken.getAuthorizedClientRegistrationId();
                    return clientService.loadAuthorizedClient(registrationId, name)
                        .switchIfEmpty(Mono.error(new OAuth2AuthorizationException(new OAuth2Error("access_denied", "The token is expired", null))))
                        .flatMap(client -> {
                            OAuth2AccessToken accessToken = client.getAccessToken();

                            if (accessToken != null) {
                                String tokenType = accessToken.getTokenType().getValue();
                                String accessTokenValue = accessToken.getTokenValue();
                                if (isExpired(accessToken)) {
                                    log.info("AccessToken expired, refreshing automatically");
                                    return refreshToken(client, oauthToken)
                                        .switchIfEmpty(Mono.error(new OAuth2AuthorizationException(new OAuth2Error("access_denied", "The token is expired", null))))
                                        .map(m -> String.format("%s %s", tokenType, m));
                                }
                                String authorizationHeaderValue = String.format("%s %s", tokenType, accessTokenValue);
                                return Mono.just(authorizationHeaderValue);
                            }
                            return Mono.empty();
                        });
                } else if (authentication instanceof JwtAuthenticationToken) {
                    JwtAuthenticationToken accessToken = (JwtAuthenticationToken) authentication;
                    String tokenValue = accessToken.getToken().getTokenValue();
                    String authorizationHeaderValue = String.format("%s %s", OAuth2AccessToken.TokenType.BEARER.getValue(), tokenValue);
                    return Mono.just(authorizationHeaderValue);
                } else {
                    return Mono.empty();
                }
            });
    }

    private Mono<String> refreshToken(OAuth2AuthorizedClient client, OAuth2AuthenticationToken oauthToken) {
        return refreshTokenClient(client)
            .flatMap(atr -> {
                if (atr.getAccessToken() == null) {
                    log.info("Failed to refresh token for user");
                    return Mono.empty();
                }
                OAuth2RefreshToken refreshToken = atr.getRefreshToken() != null ? atr.getRefreshToken() : client.getRefreshToken();
                OAuth2AuthorizedClient updatedClient = new OAuth2AuthorizedClient(
                    client.getClientRegistration(),
                    client.getPrincipalName(),
                    atr.getAccessToken(),
                    refreshToken
                );
                return clientService.saveAuthorizedClient(updatedClient, oauthToken)
                    .then(Mono.just(atr.getAccessToken().getTokenValue()));
            });
    }

    private Mono<OAuth2AccessTokenResponse> refreshTokenClient(OAuth2AuthorizedClient currentClient) {
        MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
        formParameters.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue());
        formParameters.add(OAuth2ParameterNames.REFRESH_TOKEN, currentClient.getRefreshToken().getTokenValue());
        formParameters.add(OAuth2ParameterNames.CLIENT_ID, currentClient.getClientRegistration().getClientId());
        return webClient
            .post()
            .uri(URI.create(currentClient.getClientRegistration().getProviderDetails().getTokenUri()))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .headers(headers -> headers.setBasicAuth(
                currentClient.getClientRegistration().getClientId(),
                currentClient.getClientRegistration().getClientSecret()
            ))
            .bodyValue(formParameters)
            .retrieve()
            .bodyToMono(OAuthIdpTokenResponseDTO.class)
            .map(this::toOAuth2AccessTokenResponse)
            .onErrorMap(OAuth2AuthorizationException.class, e -> {
                log.error("Unable to refresh token", e);
                throw new OAuth2AuthenticationException(e.getError(), e);
            });
    }

    private OAuth2AccessTokenResponse toOAuth2AccessTokenResponse(OAuthIdpTokenResponseDTO oAuthIdpResponse) {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("id_token", oAuthIdpResponse.getIdToken());
        additionalParameters.put("not-before-policy", oAuthIdpResponse.getNotBefore());
        additionalParameters.put("refresh_expires_in", oAuthIdpResponse.getRefreshExpiresIn());
        additionalParameters.put("session_state", oAuthIdpResponse.getSessionState());
        return OAuth2AccessTokenResponse
            .withToken(oAuthIdpResponse.getAccessToken())
            .expiresIn(oAuthIdpResponse.getExpiresIn())
            .refreshToken(oAuthIdpResponse.getRefreshToken())
            .scopes(Pattern.compile("\\s").splitAsStream(oAuthIdpResponse.getScope()).collect(Collectors.toSet()))
            .tokenType(OAuth2AccessToken.TokenType.BEARER)
            .additionalParameters(additionalParameters)
            .build();
    }

    private boolean isExpired(OAuth2AccessToken accessToken) {
        Instant now = Instant.now();
        Instant expiresAt = accessToken.getExpiresAt();
        return now.isAfter(expiresAt.minus(Duration.ofMinutes(1L)));
    }
}
