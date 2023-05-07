package org.nentangso.core.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class NotFoundException extends HttpStatusCodeException {
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null);
    }
}
