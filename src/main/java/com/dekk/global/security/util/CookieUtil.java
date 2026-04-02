package com.dekk.global.security.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${app.cookie.domain}")
    private String domain;

    @Value("${app.cookie.secure}")
    private boolean secure;

    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .maxAge(maxAge);

        if (domain != null && !domain.isBlank()) {
            cookieBuilder.domain(domain);
        }

        response.addHeader("Set-Cookie", cookieBuilder.build().toString());
    }

    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, "")
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .maxAge(0);

        if (domain != null && !domain.isBlank()) {
            cookieBuilder.domain(domain);
        }

        response.addHeader("Set-Cookie", cookieBuilder.build().toString());
    }
}
