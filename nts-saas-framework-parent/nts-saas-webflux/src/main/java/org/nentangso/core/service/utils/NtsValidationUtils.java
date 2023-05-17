package org.nentangso.core.service.utils;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.service.errors.NtsValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class NtsValidationUtils {
    private NtsValidationUtils() {
    }

    public static <T> void validateObject(T entity) {
        validateObject(entity, null);
    }

    public static <T> void validateObject(T entity, String prefix) {
        Map<String, List<String>> errors = validateObjectAndGetErrors(entity, prefix);
        if (!errors.isEmpty()) {
            throw new NtsValidationException(errors);
        }
    }

    public static <T> Map<String, List<String>> validateObjectAndGetErrors(T entity, String prefix) {
        Map<String, List<String>> errors = new HashMap<>();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                String field = StringUtils.join(prefix, constraintViolation.getPropertyPath().toString());
                List<String> messages = errors.getOrDefault(field, new ArrayList<>());
                messages.add(constraintViolation.getMessage());
                errors.putIfAbsent(field, messages);
            }
        }
        return errors;
    }
}
