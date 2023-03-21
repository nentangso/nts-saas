package org.nentangso.core.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.nentangso.core.security.oauth2.AuthorizationHeaderUtil;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

public class TokenRelayRequestInterceptor implements RequestInterceptor {
    private final AuthorizationHeaderUtil authorizationHeaderUtil;

    public TokenRelayRequestInterceptor(AuthorizationHeaderUtil authorizationHeaderUtil) {
        super();
        this.authorizationHeaderUtil = authorizationHeaderUtil;
    }

    @Override
    public void apply(RequestTemplate template) {
        Optional<String> authorizationHeader = authorizationHeaderUtil.getAuthorizationHeader();
        authorizationHeader.ifPresent(s -> template.header(HttpHeaders.AUTHORIZATION, s));
    }
}
