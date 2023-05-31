package org.nentangso.core.client;

import org.nentangso.core.config.NtsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import reactivefeign.client.ReactiveHttpRequestInterceptor;

public class NtsAuthorizedFeignConfiguration {
    private final String clientRegistrationId;

    public NtsAuthorizedFeignConfiguration(NtsProperties ntsProperties) {
        this.clientRegistrationId = ntsProperties.getClient().getAuthorized().getClientRegistrationId();
    }

    @Bean(name = "oAuth2RequestInterceptor")
    @Profile("!testdev & !testprod")
    public ReactiveHttpRequestInterceptor getOAuth2RequestInterceptor(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        return new NtsOAuth2ClientCredentialsRequestInterceptor(
            authorizedClientManager,
            clientRegistrationId
        );
    }

    @Bean
    @Profile("!testdev & !testprod")
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
        final ReactiveClientRegistrationRepository clientRegistrationRepository,
        final ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientService
        );
    }
}
