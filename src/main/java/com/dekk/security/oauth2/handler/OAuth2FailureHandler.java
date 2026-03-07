package com.dekk.security.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final String redirectUri;

    public OAuth2FailureHandler(@Value("${app.oauth2.redirect-uri}") String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 authentication failed: {}", exception.getMessage());

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri);

        String message = exception.getMessage();
        if (message != null && message.startsWith("EA40901:")) {
            String existingProvider = message.split(":")[1];
            builder.queryParam("error", "DUPLICATE_EMAIL")
                    .queryParam("provider", existingProvider);
        } else {
            builder.queryParam("error", exception.getLocalizedMessage());
        }

        getRedirectStrategy().sendRedirect(request, response, builder.build().toUriString());
    }
}
