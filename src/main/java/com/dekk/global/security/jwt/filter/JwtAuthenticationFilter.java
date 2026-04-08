package com.dekk.global.security.jwt.filter;

import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
import com.dekk.app.admin.security.AdminUserDetails;
import com.dekk.app.auth.domain.exception.AuthBusinessException;
import com.dekk.app.auth.domain.exception.AuthErrorCode;
import com.dekk.global.error.BusinessException;
import com.dekk.global.error.ErrorCode;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ADMIN_TOKEN_COOKIE_NAME = "admin_access_token";

    private static final String[] EXCLUDE_PATHS = {
        "/w/v1/auth/refresh",
        "/adm/v1/auth/login",
        "/oauth2/authorization/**",
        "/login/oauth2/code/**",
        "/i/v1/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/v3/api-docs",
        "/actuator/**"
    };

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final AdminTokenBlackListRepository adminTokenBlackListRepository;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            ObjectMapper objectMapper,
            AdminTokenBlackListRepository adminTokenBlackListRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
        this.adminTokenBlackListRepository = adminTokenBlackListRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pattern : EXCLUDE_PATHS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String jwt = null;
        boolean isAdminRequest = requestUri.startsWith("/adm/");

        if (isAdminRequest) {
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

                if (authentication.getPrincipal() instanceof AdminUserDetails) {
                    if (adminTokenBlackListRepository.isBlackListed(jwt)) {
                        throw new AdminBusinessException(AdminErrorCode.BLACKLISTED_TOKEN);
                    }
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (BusinessException e) {
            handleAuthenticationException(response, e.errorCode());
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

    private void handleAuthenticationException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.status().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.from(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
