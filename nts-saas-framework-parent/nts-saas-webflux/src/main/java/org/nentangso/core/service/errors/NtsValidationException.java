package org.nentangso.core.service.errors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class NtsValidationException extends HttpStatusCodeException {
    private static final long serialVersionUID = 1L;

    protected final Map<String, List<String>> errors;

    public NtsValidationException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
        this.errors = new LinkedHashMap<>();
    }

    public NtsValidationException(Map<String, List<String>> errors) {
        super(
            buildMessage(errors),
            HttpStatus.UNPROCESSABLE_ENTITY,
            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
            null,
            null,
            null
        );
        this.errors = errors;
    }

    public NtsValidationException(String key, String message) {
        super(
            StringUtils.join(key, ": ", message),
            HttpStatus.UNPROCESSABLE_ENTITY,
            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
            null,
            null,
            null
        );
        this.errors = Collections.singletonMap(key, Collections.singletonList(message));
    }

    public NtsValidationException(BindingResult bindingResult) {
        super(
            buildMessage(bindingResult),
            HttpStatus.UNPROCESSABLE_ENTITY,
            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
            null,
            null,
            null
        );
        this.errors = buildErrors(bindingResult);
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public boolean hasFieldErrors() {
        return !this.errors.isEmpty();
    }

    public void addFieldError(String field, String error) {
        List<String> messages = this.errors.getOrDefault(field, new ArrayList<>());
        messages.add(error);
        this.errors.putIfAbsent(field, messages);
    }

    public void addFieldErrors(Map<String, List<String>> fieldErrors) {
        addFieldErrors(fieldErrors, null);
    }

    public void addFieldErrors(Map<String, List<String>> fieldErrors, String prefix) {
        if (CollectionUtils.isEmpty(fieldErrors)) return;
        fieldErrors.forEach((key, newMessages) -> {
            String fieldName = StringUtils.join(prefix, key);
            List<String> messages = this.errors.getOrDefault(fieldName, new ArrayList<>());
            messages.addAll(newMessages);
            this.errors.putIfAbsent(fieldName, messages);
        });
    }

    public static String buildMessage(Map<String, List<String>> fieldErrors) {
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            return null;
        }
        return fieldErrors.entrySet().stream()
            .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
            .collect(joining(", "));
    }

    public static String buildMessage(BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasFieldErrors()) {
            return null;
        }
        return bindingResult.getFieldErrors().stream()
            .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
            .collect(joining(", "));
    }

    public static Map<String, List<String>> buildErrors(BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasErrors()) {
            return Collections.emptyMap();
        }
        final Map<String, List<String>> errors = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(bindingResult.getGlobalErrors())) {
            var baseErrors = bindingResult.getGlobalErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
            errors.put("base", baseErrors);
        }
        if (!CollectionUtils.isEmpty(bindingResult.getFieldErrors())) {
            var fieldErrors = bindingResult.getFieldErrors()
                .stream()
                .collect(
                    groupingBy(
                        FieldError::getField,
                        collectingAndThen(
                            toList(),
                            items -> items.stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(toList())
                        )
                    )
                );
            errors.putAll(fieldErrors);
        }
        return errors;
    }
}