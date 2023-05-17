package org.nentangso.core.web.rest.errors;

import org.nentangso.core.service.utils.NtsTextUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.*;

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
public class NtsExceptionTranslator implements ProblemHandling, SecurityAdviceTrait {
    @Override
    public ResponseEntity<Problem> process(ResponseEntity<Problem> entity) {
        HttpStatus status = entity.getStatusCode();
        NtsProblem body = new NtsProblem();
        if (HttpStatus.UNAUTHORIZED.equals(status)) {
            body.setErrors(NtsErrorConstants.MESSAGE_UNAUTHORIZED);
        } else if (HttpStatus.FORBIDDEN.equals(status)) {
            body.setErrors(NtsErrorConstants.MESSAGE_ACCESS_DENIED);
        } else if (HttpStatus.METHOD_NOT_ALLOWED.equals(status)) {
            status = HttpStatus.NOT_ACCEPTABLE;
            body.setErrors(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());
        } else if (HttpStatus.UNPROCESSABLE_ENTITY.equals(status) && entity.getBody() != null) {
            Problem input = entity.getBody();
            if (input instanceof ConstraintViolationProblem) {
                final Map<String, List<String>> errors = ((ConstraintViolationProblem) input).getViolations().stream()
                    .collect(groupingBy(Violation::getField, mapping(Violation::getMessage, toList())));
                body.setErrors(errors);
            } else {
                body.setErrors(Collections.singletonMap(NtsErrorConstants.KEY_BASE, NtsErrorConstants.MESSAGE_UNPROCESSABLE));
            }
        } else {
            body.setErrors(status.getReasonPhrase());
        }
        return ResponseEntity.status(status)
            .headers(entity.getHeaders())
            .contentType(MediaType.APPLICATION_JSON)
            .body(body);
    }

    @Override
    public StatusType defaultConstraintViolationStatus() {
        return Status.UNPROCESSABLE_ENTITY;
    }

    @Override
    public String formatFieldName(@Nullable String fieldName) {
        return Optional.ofNullable(fieldName)
            .map(NtsTextUtils::toSnakeCase)
            .orElse(NtsErrorConstants.KEY_BASE);
    }

    @ExceptionHandler
    Mono<ResponseEntity<Problem>> handleConcurrencyFailureException(
        final ConcurrencyFailureException exception,
        final ServerWebExchange request
    ) {
        return create(Status.CONFLICT, exception, request);
    }
}
