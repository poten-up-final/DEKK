package com.dekk.admin.security;

import com.dekk.admin.domain.exception.AdminBusinessException;
import com.dekk.admin.domain.exception.AdminErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class AdminJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final AdminJwtTokenProvider adminJwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/adm/v1/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            if (StringUtils.hasText(token)) {
                if (!adminJwtTokenProvider.validateToken(token)) {
                    throw new AdminBusinessException(AdminErrorCode.INVALID_TOKEN);
                }

                Authentication authentication = adminJwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (AdminBusinessException e) {
            handleException(response, e);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX_LENGTH);
        }
        return null;
    }

    private void handleException(HttpServletResponse response, AdminBusinessException e) throws IOException {
        response.setStatus(e.getErrorCode().status().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponseDto errorDto = new ErrorResponseDto(e.getErrorCode().code(), e.getErrorCode().message());

        response.getWriter().write(objectMapper.writeValueAsString(errorDto));
    }

    private record ErrorResponseDto(String code, String message) {}
}
