/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nentangso.core.web.rest.errors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.WebUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

/**
 * A class with an {@code @ExceptionHandler} method that handles all Spring
 * WebFlux raised exceptions by returning a {@link ResponseEntity} with
 * RFC 7807 formatted error details in the body.
 *
 * <p>Convenient as a base class of an {@link ControllerAdvice @ControllerAdvice}
 * for global exception handling in an application. Subclasses can override
 * individual methods that handle a specific exception, override
 * {@link #handleExceptionInternal} to override common handling of all exceptions,
 * or {@link #createResponseEntity} to intercept the final step of creating the
 */
public abstract class ResponseEntityExceptionHandler {

    /**
     * Log category to use when no mapped handler is found for a request.
     *
     * @see #pageNotFoundLogger
     */
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";

    /**
     * Specific logger to use when no mapped handler is found for a request.
     *
     * @see #PAGE_NOT_FOUND_LOG_CATEGORY
     */
    protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);

    /**
     * Common logger for use in subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());


    /**
     * Provides handling for standard Spring MVC exceptions.
     *
     * @param ex       the target exception
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    @ExceptionHandler({
        HttpRequestMethodNotSupportedException.class,
        HttpMediaTypeNotSupportedException.class,
        HttpMediaTypeNotAcceptableException.class,
        MissingPathVariableException.class,
        MissingServletRequestParameterException.class,
        ServletRequestBindingException.class,
        ConversionNotSupportedException.class,
        TypeMismatchException.class,
        HttpMessageNotReadableException.class,
        HttpMessageNotWritableException.class,
        MethodArgumentNotValidException.class,
        MissingServletRequestPartException.class,
        BindException.class,
        AsyncRequestTimeoutException.class
    })
    @Nullable
    public final Mono<ResponseEntity<Object>> handleException(Exception ex, ServerWebExchange exchange) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof HttpRequestMethodNotSupportedException) {
            HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
            return handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) ex, headers, status, exchange);
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            return handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException) ex, headers, status, exchange);
        } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
            HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
            return handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException) ex, headers, status, exchange);
        } else if (ex instanceof MissingPathVariableException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleMissingPathVariable((MissingPathVariableException) ex, headers, status, exchange);
        } else if (ex instanceof MissingServletRequestParameterException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleMissingServletRequestParameter((MissingServletRequestParameterException) ex, headers, status, exchange);
        } else if (ex instanceof ServletRequestBindingException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleServletRequestBindingException((ServletRequestBindingException) ex, headers, status, exchange);
        } else if (ex instanceof ConversionNotSupportedException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleConversionNotSupported((ConversionNotSupportedException) ex, headers, status, exchange);
        } else if (ex instanceof TypeMismatchException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleTypeMismatch((TypeMismatchException) ex, headers, status, exchange);
        } else if (ex instanceof HttpMessageNotReadableException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleHttpMessageNotReadable((HttpMessageNotReadableException) ex, headers, status, exchange);
        } else if (ex instanceof HttpMessageNotWritableException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleHttpMessageNotWritable((HttpMessageNotWritableException) ex, headers, status, exchange);
        } else if (ex instanceof MethodArgumentNotValidException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleMethodArgumentNotValid((MethodArgumentNotValidException) ex, headers, status, exchange);
        } else if (ex instanceof MissingServletRequestPartException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleMissingServletRequestPart((MissingServletRequestPartException) ex, headers, status, exchange);
        } else if (ex instanceof BindException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleBindException((BindException) ex, headers, status, exchange);
        } else if (ex instanceof AsyncRequestTimeoutException) {
            HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
            return handleAsyncRequestTimeoutException((AsyncRequestTimeoutException) ex, headers, status, exchange);
        } else {
            // Unknown exception, typically a wrapper with a common MVC exception as cause
            // (since @ExceptionHandler type declarations also match first-level causes):
            // We only deal with top-level MVC exceptions here, so let's rethrow the given
            // exception for further processing through the HandlerExceptionResolver chain.
            return Mono.error(ex);
        }
    }

    /**
     * Customize the response for HttpRequestMethodNotSupportedException.
     * <p>This method logs a warning, sets the "Allow" header, and delegates to
     * {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        pageNotFoundLogger.warn(ex.getMessage());

        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }
        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for HttpMediaTypeNotSupportedException.
     * <p>This method sets the "Accept" header and delegates to
     * {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleHttpMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
            if (exchange.getRequest() instanceof ServletWebRequest) {
                ServletWebRequest servletWebRequest = (ServletWebRequest) exchange.getRequest();
                if (HttpMethod.PATCH.equals(servletWebRequest.getHttpMethod())) {
                    headers.setAcceptPatch(mediaTypes);
                }
            }
        }

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for HttpMediaTypeNotAcceptableException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleHttpMediaTypeNotAcceptable(
        HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for MissingPathVariableException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     * @since 4.2
     */
    protected Mono<ResponseEntity<Object>> handleMissingPathVariable(
        MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for MissingServletRequestParameterException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for ServletRequestBindingException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleServletRequestBindingException(
        ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for ConversionNotSupportedException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleConversionNotSupported(
        ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for TypeMismatchException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleTypeMismatch(
        TypeMismatchException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for HttpMessageNotReadableException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for HttpMessageNotWritableException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleHttpMessageNotWritable(
        HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for MethodArgumentNotValidException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for MissingServletRequestPartException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleMissingServletRequestPart(
        MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for BindException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex       the exception
     * @param headers  the headers to be written to the response
     * @param status   the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleBindException(
        BindException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * Customize the response for AsyncRequestTimeoutException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex         the exception
     * @param headers    the headers to be written to the response
     * @param status     the selected response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    @Nullable
    protected Mono<ResponseEntity<Object>> handleAsyncRequestTimeoutException(
        AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        if (exchange.getResponse().isCommitted()) {
            if (logger.isWarnEnabled()) {
                logger.warn("Async request timed out");
            }
            return null;
        }

        return handleExceptionInternal(ex, null, headers, status, exchange);
    }

    /**
     * A single place to customize the response body of all exception types.
     *
     * @param ex       the exception
     * @param body     the body for the response
     * @param headers  the headers for the response
     * @param status   the response status
     * @param exchange the current request and response
     * @return a {@code Mono} with the {@code ResponseEntity} for the response
     */
    protected Mono<ResponseEntity<Object>> handleExceptionInternal(
        Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        return createResponseEntity(body, headers, status, exchange);
    }


    /**
     * Create the {@link ResponseEntity} to use from the given body, headers,
     * and statusCode. Subclasses can override this method to inspect and possibly
     * modify the body, headers, or statusCode, e.g. to re-create an instance of
     *
     * @param body     the body to use for the response
     * @param headers  the headers to use for the response
     * @param status   the status code to use for the response
     * @param exchange the current request and response
     * @return a {@code Mono} with the created {@code ResponseEntity}
     */
    protected Mono<ResponseEntity<Object>> createResponseEntity(
        @Nullable Object body, HttpHeaders headers, HttpStatus status, ServerWebExchange exchange) {

        return Mono.just(new ResponseEntity<>(body, headers, status));
    }
}
