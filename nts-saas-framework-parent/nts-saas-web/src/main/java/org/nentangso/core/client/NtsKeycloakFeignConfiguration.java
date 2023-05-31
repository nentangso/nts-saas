package org.nentangso.core.client;

import feign.RequestInterceptor;
import org.nentangso.core.config.NtsKeycloakLocationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class NtsKeycloakFeignConfiguration {
    @Bean(name = "ntsKeycloakRequestInterceptor")
    public RequestInterceptor getOAuth2RequestInterceptor(
        OAuth2AuthorizedClientManager authorizedClientManager,
        NtsKeycloakLocationProperties keycloakLocationProperties
    ) {
        return new NtsOAuth2ClientCredentialsRequestInterceptor(
            authorizedClientManager,
            keycloakLocationProperties.getClientRegistrationId()
        );
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
        final ClientRegistrationRepository clientRegistrationRepository,
        final OAuth2AuthorizedClientService authorizedClientService
    ) {
        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientService
        );
    }
}
