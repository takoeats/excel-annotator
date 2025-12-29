package io.github.takoeats.excelannotator.internal.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseHeaderHandler {

    public static void setResponseHeaders(HttpServletResponse response, String contentType,
                                          String fallbackFileName, String encodedFileName) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fallbackFileName + "\"; filename*=UTF-8''" + encodedFileName);
        setDefaultCacheControlIfAbsent(response);
    }

    public static void setDefaultCacheControlIfAbsent(HttpServletResponse response) {
        if (response.getHeader("Cache-Control") == null) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        }
    }
}
