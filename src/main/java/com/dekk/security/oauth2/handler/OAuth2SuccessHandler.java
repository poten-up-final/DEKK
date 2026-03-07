package com.dekk.security.oauth2.handler;

import com.dekk.auth.jwt.JwtTokenProvider;
import com.dekk.security.oauth2.repository.InMemoryOAuth2AuthorizationRequestRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final InMemoryOAuth2AuthorizationRequestRepository inMemoryRepository;
    private final String defaultRedirectUri;

    public OAuth2SuccessHandler(
            JwtTokenProvider jwtTokenProvider,
            InMemoryOAuth2AuthorizationRequestRepository inMemoryRepository,
            @Value("${app.oauth2.redirect-uri}") String defaultRedirectUri) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.inMemoryRepository = inMemoryRepository;
        this.defaultRedirectUri = defaultRedirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        String state = request.getParameter("state");

        String requestedRedirectUri = inMemoryRepository.getRedirectUriAndRemove(state);

        String targetUrl = StringUtils.hasText(requestedRedirectUri) ? requestedRedirectUri : defaultRedirectUri;

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
