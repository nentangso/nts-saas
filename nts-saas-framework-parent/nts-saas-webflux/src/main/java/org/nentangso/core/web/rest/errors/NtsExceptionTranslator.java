package org.nentangso.core.web.rest.errors;

import org.nentangso.core.service.utils.NtsTextUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static org.zalando.problem.Status.UNPROCESSABLE_ENTITY;

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
        NtsProblem body = new NtsProblem();
        if (HttpStatus.UNAUTHORIZED.equals(entity.getStatusCode())) {
            body.setErrors(NtsErrorConstants.MESSAGE_UNAUTHORIZED);
        } else if (HttpStatus.FORBIDDEN.equals(entity.getStatusCode())) {
            body.setErrors(NtsErrorConstants.MESSAGE_ACCESS_DENIED);
        } else if (HttpStatus.UNPROCESSABLE_ENTITY.equals(entity.getStatusCode()) && entity.getBody() != null) {
            Problem input = entity.getBody();
            if (input instanceof ConstraintViolationProblem) {
                final Map<String, List<String>> errors = ((ConstraintViolationProblem) input).getViolations().stream()
                    .collect(groupingBy(Violation::getField, mapping(Violation::getMessage, toList())));
                body.setErrors(errors);
            } else {
                body.setErrors(Collections.singletonMap(NtsErrorConstants.KEY_BASE, NtsErrorConstants.MESSAGE_UNPROCESSABLE));
            }
        } else {
            body.setErrors(entity.getStatusCode().getReasonPhrase());
        }
        return ResponseEntity.status(entity.getStatusCode())
            .headers(entity.getHeaders())
            .body(body);
    }

    @Override
    public StatusType defaultConstraintViolationStatus() {
        return UNPROCESSABLE_ENTITY;
    }

    @Override
    public String formatFieldName(String fieldName) {
        return NtsTextUtils.toSnakeCase(fieldName);
    }
}
