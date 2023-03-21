package org.nentangso.core.web.rest.errors;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.service.errors.FormValidationException;
import org.nentangso.core.service.errors.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public class NtsExceptionTranslator extends ResponseEntityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
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
    protected ResponseEntity<Object> handleNtsException(Exception ex, WebRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof AuthenticationException) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return handleAuthentication((AuthenticationException) ex, headers, status, request);
        } else if (ex instanceof AccessDeniedException) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleAccessDenied((AccessDeniedException) ex, headers, status, request);
        } else if (ex instanceof ResponseStatusException) {
            return handleResponseStatus((ResponseStatusException) ex, headers, null, request);
        } else if (ex instanceof ConcurrencyFailureException) {
            HttpStatus status = HttpStatus.CONFLICT;
            return handleConcurrencyFailure((ConcurrencyFailureException) ex, headers, status, request);
        } else if (ex instanceof NotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleNotFound((NotFoundException) ex, headers, status, request);
        } else if (ex instanceof BadRequestAlertException) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            return handleBadRequestAlert((BadRequestAlertException) ex, headers, status, request);
        } else if (ex instanceof FormValidationException) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            return handleFormValidation((FormValidationException) ex, headers, status, request);
        } else {
            // Unknown exception, typically a wrapper with a common MVC exception as cause
            // (since @ExceptionHandler type declarations also match first-level causes):
            // We only deal with top-level MVC exceptions here, so let's rethrow the given
            // exception for further processing through the HandlerExceptionResolver chain.
            throw ex;
        }
    }

    private ResponseEntity<Object> handleAuthentication(AuthenticationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn(ex.getMessage());
        headers.set(HttpHeaders.WWW_AUTHENTICATE, generateAuthenticateHeader(ex, headers, status, request));
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Deprecated(since = "1.1.5")
    protected String generateAuthenticateHeader(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return String.format("Basic realm=\"%s\"", realmName);
    }

    private ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn(ex.getMessage());
        headers.set(HttpHeaders.WWW_AUTHENTICATE, generateAuthenticateHeader(ex, headers, status, request));
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    private ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex, null, headers, ex.getStatus(), request);
    }

    private ResponseEntity<Object> handleConcurrencyFailure(ConcurrencyFailureException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn(ex.getMessage());

        return handleExceptionInternal(ex, null, headers, status, request);
    }

    private ResponseEntity<Object> handleNotFound(NotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex, null, headers, status, request);
    }

    private ResponseEntity<Object> handleBadRequestAlert(BadRequestAlertException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex, null, headers, status, request);
    }

    private ResponseEntity<Object> handleFormValidation(FormValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleConversionNotSupported(ex, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleMissingPathVariable(ex, headers, HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleHttpRequestMethodNotSupported(ex, headers, HttpStatus.NOT_ACCEPTABLE, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleHttpMediaTypeNotSupported(ex, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleServletRequestBindingException(ex, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleMethodArgumentNotValid(ex, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleMissingServletRequestPart(ex, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleMissingServletRequestParameter(ex, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleBindException(ex, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
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
            String errors = StringUtils.defaultIfBlank(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            body = Collections.singletonMap(NtsErrorConstants.KEY_ERRORS, errors);
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    protected Map<String, List<String>> buildUnprocessableErrors(Exception ex) {
        Map<String, List<String>> errors = Collections.singletonMap(NtsErrorConstants.KEY_BASE, Collections.singletonList(NtsErrorConstants.MESSAGE_UNPROCESSABLE));
        if (ex instanceof FormValidationException && !((FormValidationException) ex).getErrors().isEmpty()) {
            errors = ((FormValidationException) ex).getErrors();
        } else if (ex instanceof BadRequestAlertException) {
            errors = Collections.singletonMap(((BadRequestAlertException) ex).getErrorKey(), Collections.singletonList(ex.getMessage()));
        } else if (ex instanceof BindException) {
            errors = FormValidationException.buildErrors(((BindException) ex).getBindingResult());
        } else if (ex instanceof MissingServletRequestParameterException) {
            errors = Collections.singletonMap(((MissingServletRequestParameterException) ex).getParameterName(), Collections.singletonList(ex.getMessage()));
        } else if (ex instanceof MissingServletRequestPartException) {
            errors = Collections.singletonMap(((MissingServletRequestPartException) ex).getRequestPartName(), Collections.singletonList(ex.getMessage()));
        }
        return errors;
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleInternalServerError(Exception ex, WebRequest request) {
        log.error("Internal Server Error", ex);
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    /**
     * Collect error details from the provided parameters and format according to RFC
     * 6750, specifically {@code error}, {@code error_description}, {@code error_uri}, and
     * {@code scope}.
     *
     * @param request       that resulted in an <code>AuthenticationException</code>
     * @param response      so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Override
    @ExceptionHandler(AuthenticationException.class)
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        HttpStatus status = getAuthenticationStatus(authException);
        Map<String, String> parameters = createAuthenticationParameters(authException);
        respond(response, status, parameters, NtsErrorConstants.MESSAGE_UNAUTHORIZED);
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
            if (org.springframework.util.StringUtils.hasText(error.getDescription())) {
                parameters.put("error_description", error.getDescription());
            }
            if (org.springframework.util.StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }
            if (error instanceof BearerTokenError) {
                BearerTokenError bearerTokenError = (BearerTokenError) error;
                if (org.springframework.util.StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }
            }
        }
        return parameters;
    }

    private static void respond(HttpServletResponse response, HttpStatus status, Map<String, String> parameters, String errors) throws IOException {
        String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
        response.setStatus(status.value());
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String responseBody = String.format("{\"errors\":\"%s\"}", errors);
        response.getWriter().write(responseBody);
    }

    private static String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
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

    /**
     * Collect error details from the provided parameters and format according to RFC
     * 6750, specifically {@code error}, {@code error_description}, {@code error_uri}, and
     * {@code scope}.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     */
    @Override
    @ExceptionHandler(AccessDeniedException.class)
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        Map<String, String> parameters = createAccessDeniedParameters(request);
        String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }

    private Map<String, String> createAccessDeniedParameters(HttpServletRequest request) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }
        if (request.getUserPrincipal() instanceof AbstractOAuth2TokenAuthenticationToken) {
            parameters.put("error", BearerTokenErrorCodes.INSUFFICIENT_SCOPE);
            parameters.put("error_description", "The request requires higher privileges than provided by the access token.");
            parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
        }
        return parameters;
    }
}
