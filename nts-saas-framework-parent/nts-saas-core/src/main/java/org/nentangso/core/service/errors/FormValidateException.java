package org.nentangso.core.service.errors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class FormValidateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected final Map<String, List<String>> fieldErrors;

    public FormValidateException() {
        this.fieldErrors = new LinkedHashMap<>();
    }

    public FormValidateException(Map<String, List<String>> fieldErrors) {
        super(buildMessage(fieldErrors));
        this.fieldErrors = fieldErrors;
    }

    public FormValidateException(String key, String message) {
        super(StringUtils.join(key, ": ", message));
        this.fieldErrors = Collections.singletonMap(key, Collections.singletonList(message));
    }

    public FormValidateException(BindingResult bindingResult) {
        super(buildMessage(bindingResult));
        this.fieldErrors = buildFieldErrors(bindingResult);
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public boolean hasFieldErrors() {
        return !this.fieldErrors.isEmpty();
    }

    public void addFieldError(String field, String error) {
        List<String> messages = this.fieldErrors.getOrDefault(field, new ArrayList<>());
        messages.add(error);
        this.fieldErrors.putIfAbsent(field, messages);
    }

    public void addFieldErrors(Map<String, List<String>> fieldErrors) {
        addFieldErrors(fieldErrors, null);
    }

    public void addFieldErrors(Map<String, List<String>> fieldErrors, String prefix) {
        if (CollectionUtils.isEmpty(fieldErrors)) return;
        fieldErrors.forEach((key, newMessages) -> {
            String fieldName = StringUtils.join(prefix, key);
            List<String> messages = this.fieldErrors.getOrDefault(fieldName, new ArrayList<>());
            messages.addAll(newMessages);
            this.fieldErrors.putIfAbsent(fieldName, messages);
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

    public static Map<String, List<String>> buildFieldErrors(BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasFieldErrors()) {
            return Collections.emptyMap();
        }
        return bindingResult.getFieldErrors().stream()
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
    }
}
