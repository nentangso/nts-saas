package org.nentangso.core.web.rest.errors;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.service.errors.FormValidateException;
import org.nentangso.core.service.errors.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.env.Environment;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.*;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;
import reactor.core.publisher.Mono;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.web.util.HeaderUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.*;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@ControllerAdvice
@ConditionalOnMissingBean(name = "exceptionTranslator")
public class NtsExceptionTranslator implements ProblemHandling, SecurityAdviceTrait {

    private static final String ERRORS_KEY = "errors";
    private static final String MESSAGE_KEY = "message";
    private static final String PATH_KEY = "path";
    private static final String VIOLATIONS_KEY = "violations";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Environment env;

    public NtsExceptionTranslator(Environment env) {
        this.env = env;
    }

    /**
     * Post-process the Problem payload to add the message key for the front-end if needed.
     */
    @Override
    public Mono<ResponseEntity<Problem>> process(@Nullable ResponseEntity<Problem> entity, ServerWebExchange request) {
        if (entity == null) {
            return Mono.empty();
        }
        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return Mono.just(entity);
        }

        ProblemBuilder builder = Problem
            .builder()
            .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? NtsErrorConstants.DEFAULT_TYPE : problem.getType())
            .withStatus(problem.getStatus())
            .withTitle(problem.getTitle())
            .with(PATH_KEY, request.getRequest().getPath().value());

        if (problem instanceof ConstraintViolationProblem) {
            builder
                .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations())
                .with(MESSAGE_KEY, NtsErrorConstants.ERR_VALIDATION);
        } else {
            builder.withCause(((DefaultProblem) problem).getCause()).withDetail(problem.getDetail()).withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return Mono.just(new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode()));
    }

    @Override
    public Mono<ResponseEntity<Problem>> handleBindingResult(WebExchangeBindException ex, @Nonnull ServerWebExchange request) {
        Map<String, List<String>> fieldErrors = FormValidateException.buildFieldErrors(ex.getBindingResult());
        Problem problem = createProblemFromFieldErrors(fieldErrors);
        return create(ex, problem, request);
    }

    private Problem createProblemFromFieldErrors(Map<String, List<String>> fieldErrors) {
        return Problem
            .builder()
            .withType(NtsErrorConstants.CONSTRAINT_VIOLATION_TYPE)
            .withTitle("Method argument not valid")
            .withStatus(defaultConstraintViolationStatus())
            .with(MESSAGE_KEY, NtsErrorConstants.ERR_VALIDATION)
            .with(ERRORS_KEY, fieldErrors)
            .build();
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Problem>> handleBadRequestAlertException(BadRequestAlertException ex, ServerWebExchange request) {
        return create(
            ex,
            request,
            HeaderUtil.createFailureAlert(applicationName, true, ex.getEntityName(), ex.getErrorKey(), ex.getMessage())
        );
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Problem>> handleConcurrencyFailure(ConcurrencyFailureException ex, ServerWebExchange request) {
        Problem problem = Problem.builder().withStatus(Status.CONFLICT).with(MESSAGE_KEY, NtsErrorConstants.ERR_CONCURRENCY_FAILURE).build();
        return create(ex, problem, request);
    }

    @Override
    public ProblemBuilder prepare(final Throwable throwable, final StatusType status, final URI type) {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());

        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            if (throwable instanceof HttpMessageConversionException) {
                return Problem
                    .builder()
                    .withType(type)
                    .withTitle(status.getReasonPhrase())
                    .withStatus(status)
                    .withDetail("Unable to convert http message")
                    .withCause(
                        Optional.ofNullable(throwable.getCause()).filter(cause -> isCausalChainsEnabled()).map(this::toProblem).orElse(null)
                    );
            }
            if (throwable instanceof DataAccessException) {
                return Problem
                    .builder()
                    .withType(type)
                    .withTitle(status.getReasonPhrase())
                    .withStatus(status)
                    .withDetail("Failure during data access")
                    .withCause(
                        Optional.ofNullable(throwable.getCause()).filter(cause -> isCausalChainsEnabled()).map(this::toProblem).orElse(null)
                    );
            }
            if (containsPackageName(throwable.getMessage())) {
                return Problem
                    .builder()
                    .withType(type)
                    .withTitle(status.getReasonPhrase())
                    .withStatus(status)
                    .withDetail("Unexpected runtime exception")
                    .withCause(
                        Optional.ofNullable(throwable.getCause()).filter(cause -> isCausalChainsEnabled()).map(this::toProblem).orElse(null)
                    );
            }
        }

        return Problem
            .builder()
            .withType(type)
            .withTitle(status.getReasonPhrase())
            .withStatus(status)
            .withDetail(throwable.getMessage())
            .withCause(
                Optional.ofNullable(throwable.getCause()).filter(cause -> isCausalChainsEnabled()).map(this::toProblem).orElse(null)
            );
    }

    private boolean containsPackageName(String message) {
        // This list is for sure not complete
        return StringUtils.containsAny(message, "org.", "java.", "net.", "javax.", "com.", "io.", "de.", "org.nentangso.core");
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Problem>> handleFormValidateException(FormValidateException ex, ServerWebExchange request) {
        Problem problem = createProblemFromFieldErrors(ex.getFieldErrors());
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Problem>> handleNotFoundException(NotFoundException ex, ServerWebExchange request) {
        ThrowableProblem problem = Problem.builder()
            .withType(NtsErrorConstants.DEFAULT_TYPE)
            .withTitle("Not Found")
            .withStatus(Status.NOT_FOUND)
            .withDetail("404 NOT_FOUND")
            .with(MESSAGE_KEY, "error.http.404")
            .with(ERRORS_KEY, Collections.singletonMap("base", Collections.singletonList("Not Found")))
            .build();
        return create(ex, problem, request);
    }
}
