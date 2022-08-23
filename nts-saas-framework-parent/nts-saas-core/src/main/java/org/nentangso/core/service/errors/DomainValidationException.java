package org.nentangso.core.service.errors;

import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class DomainValidationException extends FormValidateException {
    private static final long serialVersionUID = 1L;

    public DomainValidationException(Map<String, List<String>> fieldErrors) {
        super(fieldErrors);
    }

    public DomainValidationException(String key, String message) {
        super(key, message);
    }

    public DomainValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
