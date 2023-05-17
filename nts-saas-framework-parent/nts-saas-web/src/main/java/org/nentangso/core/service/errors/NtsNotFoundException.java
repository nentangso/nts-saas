package org.nentangso.core.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NtsNotFoundException extends RuntimeException {
    public NtsNotFoundException() {
    }

    public NtsNotFoundException(String message) {
        super(message);
    }

    public NtsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NtsNotFoundException(Throwable cause) {
        super(cause);
    }

    public NtsNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
