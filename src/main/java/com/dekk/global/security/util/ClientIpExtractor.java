package com.dekk.global.security.util;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIpExtractor {

    private ClientIpExtractor() {}

    public static String extract(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
