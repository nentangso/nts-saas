package org.nentangso.core.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.nentangso.core.security.oauth2.AuthorizationHeaderUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class NtsAuthorizedUserFeignConfiguration {
    @Bean(name = "oAuth2RequestInterceptor")
    @Profile("!testdev & !testprod")
    public RequestInterceptor getOAuth2RequestInterceptor(AuthorizationHeaderUtil authorizationHeaderUtil) {
        return template -> {
            authenticationHeader(authorizationHeaderUtil, template);
        };
    }

    private void authenticationHeader(AuthorizationHeaderUtil authorizationHeaderUtil, RequestTemplate template) {
        Optional<String> authorizationHeader = authorizationHeaderUtil.getAuthorizationHeader();
        if (authorizationHeader.isPresent()) {
            template.header(HttpHeaders.AUTHORIZATION, authorizationHeader.get());
            return;
        }
        Optional<String> cookieHeader = getCookieHeader();
        cookieHeader.ifPresent(s -> template.header(HttpHeaders.COOKIE, s));
    }

    private Optional<String> getCookieHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String cookie = request.getHeader(HttpHeaders.COOKIE);
        return Optional.ofNullable(cookie);
    }
}
