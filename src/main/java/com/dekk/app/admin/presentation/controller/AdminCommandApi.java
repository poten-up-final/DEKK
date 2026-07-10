package com.dekk.app.admin.presentation.controller;

import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.presentation.request.AdminInviteRequest;
import com.dekk.app.admin.presentation.request.AdminSignupRequest;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "관리자 계정 제어 API", description = "최고 관리자 전용 초대, 가입 승인 및 강제 킥아웃(Kill-Switch) API")
public interface AdminCommandApi {

    @Operation(summary = "신규 관리자 초대 링크 발송", description = "오직 SUPER_ADMIN 권한을 가진 계정만 호출할 수 있습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "관리자 초대 성공 (SAD20003)")
    @ApiErrorExceptions(AdminErrorCode.class)
    ResponseEntity<ApiResponse<Void>> inviteAdmin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "초대할 관리자 정보")
                    AdminInviteRequest request,
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) Long inviterId);

    @Operation(summary = "관리자 가입 완료 (초대 수락)", description = "이메일 초대 링크를 통해 받은 토큰과 설정할 비밀번호, 소속을 전송하여 가입을 완료합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "관리자 계정 생성 완료 (SAD20101)")
    @ApiErrorExceptions(AdminErrorCode.class)
    ResponseEntity<ApiResponse<Void>> completeSignup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "가입 완료 정보") AdminSignupRequest request,
            @Parameter(hidden = true) HttpServletRequest httpRequest);

    @Operation(summary = "관리자 강제 정지 (Kill-Switch)", description = "특정 관리자의 계정을 정지시키고 접속을 즉시 차단합니다. (SUPER_ADMIN 전용)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "관리자 정지 성공 (SAD20004)")
    @ApiErrorExceptions(AdminErrorCode.class)
    ResponseEntity<ApiResponse<Void>> suspendAdmin(
            @Parameter(description = "정지시킬 관리자 ID") Long targetAdminId,
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) Long superAdminId);
}
