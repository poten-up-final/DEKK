package com.dekk.app.auth.presentation.controller;

import com.dekk.app.auth.application.AuthCommandService;
import com.dekk.app.auth.application.command.TokenRefreshCommand;
import com.dekk.app.auth.application.dto.result.TokenRefreshResult;
import com.dekk.app.auth.presentation.response.AuthResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import com.dekk.global.security.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/auth")
public class AuthController implements AuthApi {

    private final AuthCommandService authCommandService;
    private final CookieUtil cookieUtil;
    private final int accessTokenMaxAge;
    private final int refreshTokenMaxAge;

    public AuthController(
            AuthCommandService authCommandService,
            CookieUtil cookieUtil,
            @Value("${jwt.access-token-validity-in-seconds}") int accessTokenMaxAge,
            @Value("${jwt.refresh-token-validity-in-seconds}") int refreshTokenMaxAge) {
        this.authCommandService = authCommandService;
        this.cookieUtil = cookieUtil;
        this.accessTokenMaxAge = accessTokenMaxAge;
        this.refreshTokenMaxAge = refreshTokenMaxAge;
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshToken(
            @CookieValue(value = CookieUtil.REFRESH_TOKEN_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        TokenRefreshResult result = authCommandService.refreshToken(new TokenRefreshCommand(refreshToken));

        cookieUtil.addCookie(response, CookieUtil.ACCESS_TOKEN_NAME, result.accessToken(), accessTokenMaxAge);
        cookieUtil.addCookie(response, CookieUtil.REFRESH_TOKEN_NAME, result.refreshToken(), refreshTokenMaxAge);

        return ResponseEntity.ok(ApiResponse.from(AuthResultCode.TOKEN_REFRESH_SUCCESS));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@LoginUser Long userId, HttpServletResponse response) {

        if (userId != null) {
            authCommandService.logout(userId);
        }

        cookieUtil.deleteCookie(response, CookieUtil.ACCESS_TOKEN_NAME);
        cookieUtil.deleteCookie(response, CookieUtil.REFRESH_TOKEN_NAME);

        return ResponseEntity.ok(ApiResponse.from(AuthResultCode.LOGOUT_SUCCESS));
    }
}
