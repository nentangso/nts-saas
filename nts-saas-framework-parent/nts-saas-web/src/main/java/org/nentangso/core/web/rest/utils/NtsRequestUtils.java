package org.nentangso.core.web.rest.utils;

import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("unused")
public class NtsRequestUtils {
    private NtsRequestUtils() {
    }

    public static String getBody(ServletRequest request) throws IOException {
        ContentCachingRequestWrapper wrappedRequest = getWrappedRequest(request)
            .orElseThrow(() -> new UnsupportedOperationException("Application filters is not configure correctly."));
        byte[] bytes = wrappedRequest.getContentAsByteArray();
        return new String(bytes, wrappedRequest.getCharacterEncoding());
    }

    private static Optional<ContentCachingRequestWrapper> getWrappedRequest(ServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return Optional.of((ContentCachingRequestWrapper) request);
        }
        if (request instanceof ServletRequestWrapper) {
            return getWrappedRequest(((ServletRequestWrapper) request).getRequest());
        }
        return Optional.empty();
    }
}
