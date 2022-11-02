package org.nentangso.core.web.rest.errors;

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
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@ConditionalOnProperty(
    prefix = "nts.web.rest.exception-translator",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
@ControllerAdvice
@ConditionalOnMissingBean(name = "exceptionTranslator")
public class NtsExceptionTranslator extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(NtsExceptionTranslator.class);

    public static final String KEY_ERRORS = "errors";
    public static final String KEY_BASE = "base";
    public static final String MESSAGE_UNAUTHORIZED = "[API] Invalid API key or access token (unrecognized login or wrong password)";
    public static final String MESSAGE_ACCESS_DENIED = "[API] This action requires merchant approval for the necessary scope.";
    public static final String MESSAGE_UNPROCESSABLE = "Required parameter missing or invalid";

    @Value("${nts.web.rest.exception-translator.realm-name:API Authentication by nentangso.org}")
    protected String realmName;

    @ExceptionHandler({
        AuthenticationException.class,
        AccessDeniedException.class,
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

    protected String generateAuthenticateHeader(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return String.format("Basic realm=\"%s\"", realmName);
    }

    private ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn(ex.getMessage());
        headers.set(HttpHeaders.WWW_AUTHENTICATE, generateAuthenticateHeader(ex, headers, status, request));
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    private ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex, null, headers, status, request);
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
            body = Collections.singletonMap(KEY_ERRORS, MESSAGE_UNAUTHORIZED);
        } else if (HttpStatus.FORBIDDEN.equals(status) && body == null) {
            body = Collections.singletonMap(KEY_ERRORS, MESSAGE_ACCESS_DENIED);
        } else if (HttpStatus.UNPROCESSABLE_ENTITY.equals(status) && body == null) {
            Map<String, List<String>> errors = buildUnprocessableErrors(ex);
            body = Collections.singletonMap(KEY_ERRORS, errors);
        } else if (status.is4xxClientError() && body == null) {
            body = Collections.singletonMap(KEY_ERRORS, status.getReasonPhrase());
        } else if (status.is5xxServerError() && body == null) {
            body = Collections.singletonMap(KEY_ERRORS, ex.getMessage());
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    protected Map<String, List<String>> buildUnprocessableErrors(Exception ex) {
        Map<String, List<String>> errors = Collections.singletonMap(KEY_BASE, Collections.singletonList(MESSAGE_UNPROCESSABLE));
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
}
