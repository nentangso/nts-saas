package org.nentangso.core.web.rest.errors;

import org.nentangso.core.service.errors.FormValidationException;
import org.nentangso.core.service.errors.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (<a href="https://tools.ietf.org/html/rfc7807">RFC7807</a>).
 */
@ConditionalOnProperty(
    prefix = "nts.web.rest.exception-translator",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
@ControllerAdvice
@ConditionalOnMissingBean(name = "exceptionTranslator")
public class NtsExceptionTranslator extends ResponseEntityExceptionHandler implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {
    private static final Logger log = LoggerFactory.getLogger(NtsExceptionTranslator.class);

    @Value("${nts.web.rest.exception-translator.realm-name:API Authentication by nentangso.org}")
    protected String realmName;

    @ExceptionHandler({
        ResponseStatusException.class,
        ConcurrencyFailureException.class,
        NotFoundException.class,
        BadRequestAlertException.class,
        FormValidationException.class,
    })
    protected Mono<ResponseEntity<Object>> handleNtsException(Exception ex, ServerWebExchange exchange) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof AuthenticationException) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return handleAuthentication((AuthenticationException) ex, headers, status, exchange);
        } else if (ex instanceof AccessDeniedException) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleAccessDenied((AccessDeniedException) ex, headers, status, exchange);
        } else if (ex instanceof ResponseStatusException) {
            return handleResponseStatus((ResponseStatusException) ex, headers, null, exchange);
        } else if (ex instanceof ConcurrencyFailureException) {
            HttpStatus status = HttpStatus.CONFLICT;
            return handleConcurrencyFailure((ConcurrencyFailureException) ex, headers, status, exchange);
        } else if (ex instanceof NotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleNotFound((NotFoundException) ex, headers, status, exchange);
        } else if (ex instanceof BadRequestAlertException) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            return handleBadRequestAlert((BadRequestAlertException) ex, headers, status, exchange);
        } else if (ex instanceof FormValidationException) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            return handleFormValidation((FormValidationException) ex, headers, status, exchange);
        } else {
            // Unknown exception, typically a wrapper with a common MVC exception as cause
            // (since @ExceptionHandler type declarations also match first-level causes):
            // We only deal with top-level MVC exceptions here, so let's rethrow the given
            // exception for further processing through the HandlerExceptionResolver chain.
            return Mono.error(ex);
        }
    }

    private Mono<ResponseEntity<Object>> handleAuthentication(AuthenticationException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {
        log.warn(ex.getMessage());
        headers.set(HttpHeaders.WWW_AUTHENTICATE, generateAuthenticateHeader(ex, headers, status, exchange));
        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    @Deprecated(since = "1.1.5")
    protected String generateAuthenticateHeader(Exception ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {
        return String.format("Bearer realm=\"%s\"", realmName);
    }

    private Mono<ResponseEntity<Object>> handleAccessDenied(AccessDeniedException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {
        log.warn(ex.getMessage());
        headers.set(HttpHeaders.WWW_AUTHENTICATE, generateAuthenticateHeader(ex, headers, status, exchange));
        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    private Mono<ResponseEntity<Object>> handleResponseStatus(ResponseStatusException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, ex.getStatus(), exchange);
    }

    private Mono<ResponseEntity<Object>> handleConcurrencyFailure(ConcurrencyFailureException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {
        log.warn(ex.getMessage());

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    private Mono<ResponseEntity<Object>> handleNotFound(NotFoundException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    private Mono<ResponseEntity<Object>> handleBadRequestAlert(BadRequestAlertException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    private Mono<ResponseEntity<Object>> handleFormValidation(FormValidationException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {
        return super.handleConversionNotSupported(ex, headers, HttpStatus.BAD_REQUEST, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {
        return super.handleMethodArgumentNotValid(ex, headers, HttpStatus.UNPROCESSABLE_ENTITY, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {
        if (HttpStatus.UNAUTHORIZED.equals(status) && body == null) {
            body = Collections.singletonMap(NtsErrorConstants.KEY_ERRORS, NtsErrorConstants.MESSAGE_UNAUTHORIZED);
        } else if (HttpStatus.FORBIDDEN.equals(status) && body == null) {
            body = Collections.singletonMap(NtsErrorConstants.KEY_ERRORS, NtsErrorConstants.MESSAGE_ACCESS_DENIED);
        } else if (HttpStatus.UNPROCESSABLE_ENTITY.equals(status) && body == null) {
            Map<String, List<String>> errors = buildUnprocessableErrors(ex);
            body = Collections.singletonMap(NtsErrorConstants.KEY_ERRORS, errors);
        } else if (status.is4xxClientError() && body == null) {
            body = Collections.singletonMap(NtsErrorConstants.KEY_ERRORS, status.getReasonPhrase());
        } else if (status.is5xxServerError() && body == null) {
            body = Collections.singletonMap(NtsErrorConstants.KEY_ERRORS, ex.getMessage());
        }
        return super.handleExceptionInternal(ex, body, headers, status, exchange);
    }

    protected Map<String, List<String>> buildUnprocessableErrors(Exception ex) {
        Map<String, List<String>> errors = Collections.singletonMap(NtsErrorConstants.KEY_BASE, Collections.singletonList(NtsErrorConstants.MESSAGE_UNPROCESSABLE));
        if (ex instanceof FormValidationException && !((FormValidationException) ex).getErrors().isEmpty()) {
            errors = ((FormValidationException) ex).getErrors();
        } else if (ex instanceof BadRequestAlertException) {
            errors = Collections.singletonMap(((BadRequestAlertException) ex).getErrorKey(), Collections.singletonList(ex.getMessage()));
        }
        return errors;
    }

    @ExceptionHandler(Exception.class)
    protected Mono<ResponseEntity<Object>> handleInternalServerError(Exception ex, ServerWebExchange exchange) {
        log.error("Internal Server Error", ex);
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    @Override
    @ExceptionHandler(AuthenticationException.class)
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException authException) {
        return Mono.defer(() -> {
            HttpStatus status = getAuthenticationStatus(authException);
            Map<String, String> parameters = createAuthenticationParameters(authException);
            return respond(exchange, status, parameters, NtsErrorConstants.MESSAGE_UNAUTHORIZED);
        });
    }

    protected HttpStatus getAuthenticationStatus(AuthenticationException authException) {
        if (authException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
            if (error instanceof BearerTokenError) {
                return ((BearerTokenError) error).getHttpStatus();
            }
        }
        return HttpStatus.UNAUTHORIZED;
    }

    protected Map<String, String> createAuthenticationParameters(AuthenticationException authException) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }
        if (authException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
            parameters.put("error", error.getErrorCode());
            if (StringUtils.hasText(error.getDescription())) {
                parameters.put("error_description", error.getDescription());
            }
            if (StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }
            if (error instanceof BearerTokenError) {
                BearerTokenError bearerTokenError = (BearerTokenError) error;
                if (StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }
            }
        }
        return parameters;
    }

    protected Mono<Void> respond(ServerWebExchange exchange, HttpStatus status, Map<String, String> parameters, String errors) {
        String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = String.format("{\"errors\":\"%s\"}", errors);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    protected String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
                i++;
            }
        }
        return wwwAuthenticate.toString();
    }

    @Override
    @ExceptionHandler(AccessDeniedException.class)
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }
        // @formatter:off
        return exchange.getPrincipal()
            .filter(AbstractOAuth2TokenAuthenticationToken.class::isInstance)
            .map((token) -> createAccessDeniedParameters(parameters))
            .switchIfEmpty(Mono.just(parameters))
            .flatMap((params) -> respond(exchange, HttpStatus.FORBIDDEN, params, NtsErrorConstants.MESSAGE_ACCESS_DENIED));
        // @formatter:on
    }

    protected Map<String, String> createAccessDeniedParameters(Map<String, String> parameters) {
        parameters.put("error", BearerTokenErrorCodes.INSUFFICIENT_SCOPE);
        parameters.put("error_description", "The request requires higher privileges than provided by the access token.");
        parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
        return parameters;
    }
}
