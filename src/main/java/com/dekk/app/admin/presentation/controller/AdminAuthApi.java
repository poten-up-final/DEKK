package com.dekk.app.admin.presentation.controller;

import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.presentation.request.AdminLoginRequest;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

@Tag(name = "관리자 인증 API", description = "관리자 인증 관련 API")
public interface AdminAuthApi {

    @Operation(summary = "관리자 로그인", description = "이메일과 비밀번호를 사용하여 관리자 액세스 토큰을 발급받습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "관리자 로그인 성공")
    @ApiErrorExceptions(AdminErrorCode.class)
    ResponseEntity<ApiResponse<Void>> login(AdminLoginRequest request, HttpServletResponse response);

    @Operation(summary = "관리자 로그아웃", description = "어드민 토큰을 블랙리스트에 등록하고 쿠키를 만료시킵니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "관리자 로그아웃 성공")
    @ApiErrorExceptions(AdminErrorCode.class)
    ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(hidden = true) @CookieValue(value = "admin_access_token", required = false) String accessToken,
            HttpServletResponse response);
}
