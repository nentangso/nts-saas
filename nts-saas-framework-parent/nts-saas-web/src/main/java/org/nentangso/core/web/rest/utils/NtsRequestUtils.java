package org.nentangso.core.web.rest.utils;

import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@SuppressWarnings("unused")
public class NtsRequestUtils {
    private NtsRequestUtils() {
    }

    public static String getBody(HttpServletRequest request) throws IOException {
        ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper) request;
        byte[] bytes = wrappedRequest.getContentAsByteArray();
        return new String(bytes, wrappedRequest.getCharacterEncoding());
    }
}
