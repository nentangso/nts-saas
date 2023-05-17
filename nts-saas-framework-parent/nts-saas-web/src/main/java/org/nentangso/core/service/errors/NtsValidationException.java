package org.nentangso.core.service.errors;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.web.rest.errors.NtsErrorConstants;
import org.springframework.validation.BindingResult;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import java.util.*;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class NtsValidationException extends ConstraintViolationProblem {
    private static final long serialVersionUID = 1L;

    public NtsValidationException() {
        super(Status.UNPROCESSABLE_ENTITY, Collections.emptyList());
    }

    public NtsValidationException(Map<String, List<String>> errors) {
        super(Status.UNPROCESSABLE_ENTITY, createViolations(errors));
    }

    private static List<Violation> createViolations(Map<String, List<String>> errors) {
        final List<Violation> violations = new ArrayList<>();
        Optional.ofNullable(errors).orElseGet(Collections::emptyMap)
            .forEach((key, messages) -> violations.addAll(createViolations(key, messages)));
        return violations;
    }

    private static List<Violation> createViolations(String key, List<String> messages) {
        return Optional.ofNullable(messages).orElseGet(Collections::emptyList)
            .stream()
            .map(message -> createViolation(key, message))
            .collect(toList());
    }

    private static Violation createViolation(String key, String message) {
        return new Violation(
            Optional.ofNullable(key).orElse(NtsErrorConstants.KEY_BASE),
            message
        );
    }

    public NtsValidationException(String key, String message) {
        super(Status.UNPROCESSABLE_ENTITY, Collections.singletonList(createViolation(key, message)));
    }

    public NtsValidationException(BindingResult bindingResult) {
        super(Status.UNPROCESSABLE_ENTITY, createViolations(bindingResult));
    }

    private static List<Violation> createViolations(BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasErrors()) {
            return Collections.emptyList();
        }
        final List<Violation> violations = new ArrayList<>();
        bindingResult.getGlobalErrors().forEach(objectError -> violations.add(createViolation(
            NtsErrorConstants.KEY_BASE,
            objectError.getDefaultMessage()
        )));
        bindingResult.getFieldErrors().forEach(fieldError -> violations.add(createViolation(
            fieldError.getField(),
            fieldError.getDefaultMessage()
        )));
        return violations;
    }

    public void addViolation(String key, String message) {
        getViolations().add(createViolation(key, message));
    }

    public void addViolations(Map<String, List<String>> errors) {
        addViolations(errors, null);
    }

    public void addViolations(Map<String, List<String>> errors, String prefix) {
        Optional.ofNullable(errors).orElseGet(Collections::emptyMap)
            .forEach((key, messages) -> {
                String fieldName = StringUtils.join(prefix, key);
                List<Violation> violations = createViolations(fieldName, messages);
                getViolations().addAll(violations);
            });
    }
}
