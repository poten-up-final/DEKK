package com.dekk.app.admin.presentation.controller;

import com.dekk.app.admin.application.AdminAuthService;
import com.dekk.app.admin.application.dto.result.AdminLoginResult;
import com.dekk.app.admin.presentation.request.AdminLoginRequest;
import com.dekk.app.admin.presentation.response.AdminResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginAdmin;
import com.dekk.global.security.util.ClientIpExtractor;
import com.dekk.global.security.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adm/v1/auth")
@RequiredArgsConstructor
public class AdminAuthController implements AdminAuthApi {

    private static final String ADMIN_TOKEN_COOKIE_NAME = "admin_access_token";
    private static final String ADMIN_REFRESH_TOKEN_COOKIE_NAME = "admin_refresh_token";

    private final AdminAuthService adminAuthService;
    private final CookieUtil cookieUtil;

    @Value("${jwt.admin-access-token-validity-in-seconds}")
    private int accessTokenMaxAge;

    @Value("${jwt.admin-refresh-token-validity-in-seconds}")
    private int refreshTokenMaxAge;

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {
        String clientIp = ClientIpExtractor.extract(httpRequest);

        AdminLoginResult result = adminAuthService.login(request.toCommand(clientIp));

        cookieUtil.addCookie(response, ADMIN_TOKEN_COOKIE_NAME, result.accessToken(), accessTokenMaxAge);
        cookieUtil.addCookie(response, ADMIN_REFRESH_TOKEN_COOKIE_NAME, result.refreshToken(), refreshTokenMaxAge);

        return ResponseEntity.ok(ApiResponse.from(AdminResultCode.ADMIN_LOGIN_SUCCESS));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = ADMIN_TOKEN_COOKIE_NAME, required = false) String accessToken,
            @LoginAdmin Long adminId,
            HttpServletResponse response) {
        adminAuthService.logout(accessToken, adminId);

        cookieUtil.deleteCookie(response, ADMIN_TOKEN_COOKIE_NAME);
        cookieUtil.deleteCookie(response, ADMIN_REFRESH_TOKEN_COOKIE_NAME);

        return ResponseEntity.ok(ApiResponse.from(AdminResultCode.ADMIN_LOGOUT_SUCCESS));
    }
}
