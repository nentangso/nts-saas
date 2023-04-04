package org.nentangso.core.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class NtsCookieRelayInterceptedFeignConfiguration {

    @Bean(name = "oauth2RequestInterceptor")
    public RequestInterceptor getOAuth2RequestInterceptor() {
        return template -> {
            Optional<String> cookieHeader = getCookieHeader();
            cookieHeader.ifPresent(s -> template.header(HttpHeaders.COOKIE, s));
        };
    }

    private Optional<String> getCookieHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String cookie = request.getHeader(HttpHeaders.COOKIE);
        return Optional.ofNullable(cookie);
    }
}
