package com.dekk.app.auth.presentation.controller;

import com.dekk.app.auth.domain.exception.AuthErrorCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

@Tag(name = "인증/토큰 API", description = "쿠키 기반 JWT 토큰 갱신 및 로그아웃 API (HttpOnly 쿠키를 제어합니다.)")
public interface AuthApi {

    @Operation(
            summary = "Access Token 갱신 (리프레시)",
            description = "브라우저에 저장된 HttpOnly Refresh Token 쿠키를 이용해 Access Token 쿠키를 재발급 받습니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "토큰 갱신 성공 (SA20001)"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "유효하지 않거나 만료된 리프레시 토큰 (EA40101)")
            })
    @ApiErrorExceptions({AuthErrorCode.class})
    ResponseEntity<ApiResponse<Void>> refreshToken(
            @Parameter(hidden = true) @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response);

    @Operation(summary = "로그아웃", description = "서버의 Refresh Token을 삭제 처리하고, 프론트 브라우저의 JWT 쿠키를 만료시킵니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "로그아웃 성공 (SA20002)")
            })
    @ApiErrorExceptions({AuthErrorCode.class})
    ResponseEntity<ApiResponse<Void>> logout(@LoginUser Long userId, HttpServletResponse response);
}
