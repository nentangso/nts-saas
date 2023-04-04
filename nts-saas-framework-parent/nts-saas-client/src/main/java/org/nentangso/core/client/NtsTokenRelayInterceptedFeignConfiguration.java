package org.nentangso.core.client;

import feign.RequestInterceptor;
import org.nentangso.core.security.oauth2.AuthorizationHeaderUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

public class NtsTokenRelayInterceptedFeignConfiguration {

    @Bean(name = "oAuth2RequestInterceptor")
    public RequestInterceptor getOAuth2RequestInterceptor(AuthorizationHeaderUtil authorizationHeaderUtil) {
        return template -> {
            Optional<String> authorizationHeader = authorizationHeaderUtil.getAuthorizationHeader();
            authorizationHeader.ifPresent(s -> template.header(HttpHeaders.AUTHORIZATION, s));
        };
    }
}
