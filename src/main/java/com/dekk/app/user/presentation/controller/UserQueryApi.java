package com.dekk.app.user.presentation.controller;

import com.dekk.app.user.presentation.response.UserInfoResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "사용자 정보 조회 API", description = "사용자 정보 조회 API")
public interface UserQueryApi {

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 상세 프로필 정보를 조회합니다.")
    ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(@LoginUser Long userId);
}
