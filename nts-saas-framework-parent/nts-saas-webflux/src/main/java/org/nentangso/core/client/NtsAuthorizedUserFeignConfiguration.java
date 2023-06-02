package org.nentangso.core.client;

import org.nentangso.core.security.oauth2.AuthorizationHeaderUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactivefeign.utils.MultiValueMapUtils;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NtsAuthorizedUserFeignConfiguration {
    @Bean(name = "oAuth2RequestInterceptor")
    @Profile("!testdev & !testprod")
    public ReactiveHttpRequestInterceptor getOAuth2RequestInterceptor(AuthorizationHeaderUtil authorizationHeaderUtil) {
        return request -> authorizationHeaderUtil.getAuthorizationHeader()
            .map(accessToken -> {
                MultiValueMapUtils.addOrdered(request.headers(), HttpHeaders.AUTHORIZATION, accessToken);
                return true;
            })
            .switchIfEmpty(Mono.deferContextual((context) -> {
                Optional<ServerWebExchange> exchange = context.getOrEmpty(ServerWebExchange.class);
                if (exchange.isEmpty()) {
                    return Mono.just(false);
                }
                List<String> cookie = exchange.get().getRequest().getHeaders()
                    .getOrDefault(HttpHeaders.COOKIE, Collections.emptyList());
                if (cookie != null && !cookie.isEmpty()) {
                    MultiValueMapUtils.addOrdered(request.headers(), HttpHeaders.COOKIE, cookie.get(0));
                    return Mono.just(true);
                }
                return Mono.just(false);
            }))
            .thenReturn(request);
    }
}
