package org.nentangso.core.client;

import feign.RequestInterceptor;
import org.nentangso.core.config.NtsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class NtsAuthorizedFeignConfiguration {
    private final String clientRegistrationId;

    public NtsAuthorizedFeignConfiguration(NtsProperties ntsProperties) {
        this.clientRegistrationId = ntsProperties.getClient().getAuthorized().getClientRegistrationId();
    }

    @Bean(name = "oAuth2RequestInterceptor")
    @Profile("!testdev & !testprod")
    public RequestInterceptor getOAuth2RequestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return new NtsOAuth2ClientCredentialsRequestInterceptor(
            authorizedClientManager,
            clientRegistrationId
        );
    }

    @Bean
    @Profile("!testdev & !testprod")
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
