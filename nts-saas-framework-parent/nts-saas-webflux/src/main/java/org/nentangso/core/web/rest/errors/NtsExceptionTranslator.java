package org.nentangso.core.web.rest.errors;

import org.nentangso.core.service.errors.FormValidateException;
import org.nentangso.core.service.errors.NotFoundException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

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
    havingValue = "true"
)
@ControllerAdvice
@ConditionalOnMissingBean(name = "exceptionTranslator")
public class NtsExceptionTranslator {

    public static final String MESSAGE_UNAUTHORIZED = "[API] Invalid API key or access token (unrecognized login or wrong password)";
    public static final String MESSAGE_ACCESS_DENIED = "[API] This action requires merchant approval for the necessary scope.";
    public static final String MESSAGE_UNPROCESSABLE = "Required parameter missing or invalid";
    public static final String KEY_BASE = "base";

    @Value("${nts.web.rest.exception-translator.realm-name:API Authentication by nentangso.org}")
    protected String realmName;

    @ExceptionHandler
    protected ResponseEntity<Object> handleAuthentication(AuthenticationException ex, ServerWebExchange request) {
        return createUnauthorized(ex, request);
    }

    protected ResponseEntity<Object> createUnauthorized(Exception ex, ServerWebExchange request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .header(HttpHeaders.WWW_AUTHENTICATE, generateAuthenticateHeader(ex, request))
            .body(NtsErrors.singleError(MESSAGE_UNAUTHORIZED));
    }

    protected String generateAuthenticateHeader(Exception ex, ServerWebExchange request) {
        return String.format("Basic realm=\"%s\"", realmName);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, ServerWebExchange request) {
        return createForbidden(ex, request);
    }

    protected ResponseEntity<Object> createForbidden(Exception ex, ServerWebExchange request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .header(HttpHeaders.WWW_AUTHENTICATE, generateAuthenticateHeader(ex, request))
            .body(MESSAGE_ACCESS_DENIED);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex, ServerWebExchange request) {
        return createHttpStatus(ex.getStatus(), ex, request);
    }

    protected ResponseEntity<Object> createHttpStatus(HttpStatus status, Exception ex, ServerWebExchange request) {
        return ResponseEntity.status(status)
            .body(NtsErrors.singleError(status.getReasonPhrase()));
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleNotFound(NotFoundException ex, ServerWebExchange request) {
        return createNotFound(ex, request);
    }

    protected ResponseEntity<Object> createNotFound(Exception ex, ServerWebExchange request) {
        return createHttpStatus(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, ServerWebExchange request) {
        return createNotFound(ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, ServerWebExchange request) {
        return createHttpStatus(HttpStatus.NOT_ACCEPTABLE, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, ServerWebExchange request) {
        return createHttpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, ServerWebExchange request) {
        return createHttpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleConcurrencyFailure(ConcurrencyFailureException ex, ServerWebExchange request) {
        return createHttpStatus(HttpStatus.CONFLICT, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleFormValidation(FormValidateException ex, ServerWebExchange request) {
        return createUnprocessableEntity(ex.getErrors(), ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleBadRequestAlert(BadRequestAlertException ex, ServerWebExchange request) {
        var errors = Collections.singletonMap(ex.getErrorKey(), Collections.singletonList(ex.getMessage()));
        return createUnprocessableEntity(errors, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, ServerWebExchange request) {
        var errors = Collections.singletonMap(ex.getParameterName(), Collections.singletonList(MESSAGE_UNPROCESSABLE));
        return createUnprocessableEntity(errors, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, ServerWebExchange request) {
        return createBadRequest(ex, request);
    }

    protected ResponseEntity<Object> createBadRequest(Exception ex, ServerWebExchange request) {
        return createHttpStatus(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, ServerWebExchange request) {
        return createBadRequest(ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, ServerWebExchange request) {
        return createBadRequest(ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, ServerWebExchange request) {
        return createBadRequest(ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, ServerWebExchange request) {
        return createUnprocessableEntity(ex, request);
    }

    protected ResponseEntity<Object> createUnprocessableEntity(Exception ex, ServerWebExchange request) {
        var errors = Collections.singletonMap(KEY_BASE, Collections.singletonList(MESSAGE_UNPROCESSABLE));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(NtsErrors.mapErrors(errors));
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, ServerWebExchange request) {
        return createUnprocessableEntity(ex.getBindingResult(), ex, request);
    }

    protected ResponseEntity<Object> createUnprocessableEntity(BindingResult bindingResult, Exception ex, ServerWebExchange request) {
        Map<String, List<String>> errors = FormValidateException.buildErrors(bindingResult);
        return createUnprocessableEntity(errors, ex, request);
    }

    protected ResponseEntity<Object> createUnprocessableEntity(Map<String, List<String>> errors, Exception ex, ServerWebExchange request) {
        if (CollectionUtils.isEmpty(errors)) {
            return createUnprocessableEntity(ex, request);
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(NtsErrors.mapErrors(errors));
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, ServerWebExchange request) {
        var errors = Collections.singletonMap(ex.getRequestPartName(), Collections.singletonList(ex.getMessage()));
        return createUnprocessableEntity(errors, ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleBindException(BindException ex, ServerWebExchange request) {
        return createUnprocessableEntity(ex.getBindingResult(), ex, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, ServerWebExchange request) {
        return createHttpStatus(HttpStatus.GATEWAY_TIMEOUT, ex, request);
    }
}
