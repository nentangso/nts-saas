package org.nentangso.core.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class NtsClientCredentialsInterceptedFeignConfiguration {
    @Value("${nts.client.internal.oauth2.client-registration-id:internal}")
    protected String clientRegistrationId;

    @Bean(name = "oAuth2RequestInterceptor")
    public RequestInterceptor getOAuth2RequestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return new NtsClientCredentialsRequestInterceptor(
            authorizedClientManager,
            clientRegistrationId
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
