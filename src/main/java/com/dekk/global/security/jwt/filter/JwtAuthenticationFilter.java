package com.dekk.global.security.jwt.filter;

import com.dekk.app.auth.domain.exception.AuthBusinessException;
import com.dekk.app.auth.domain.exception.AuthErrorCode;
import com.dekk.global.error.ErrorResponse;
import com.dekk.global.security.jwt.JwtTokenProvider;
import com.dekk.global.security.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ADMIN_TOKEN_COOKIE_NAME = "admin_access_token";

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String jwt = null;

        if (requestUri.startsWith("/adm/")) {
            jwt = resolveTokenFromCookie(request, ADMIN_TOKEN_COOKIE_NAME);
        } else {
            jwt = resolveTokenFromCookie(request, CookieUtil.ACCESS_TOKEN_NAME);
        }

        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtTokenProvider.validateToken(jwt)) {
                if (!jwtTokenProvider.isAccessToken(jwt)) {
                    throw new AuthBusinessException(AuthErrorCode.INVALID_TOKEN_TYPE);
                }
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (AuthBusinessException e) {
            handleAuthenticationException(response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromCookie(HttpServletRequest request, String targetCookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> targetCookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private void handleAuthenticationException(HttpServletResponse response, AuthBusinessException e)
            throws IOException {
        response.setStatus(e.errorCode().status().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.from(e.errorCode());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
