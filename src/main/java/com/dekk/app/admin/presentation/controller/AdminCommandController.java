package com.dekk.app.admin.presentation.controller;

import com.dekk.app.admin.application.AdminCommandService;
import com.dekk.app.admin.presentation.request.AdminInviteRequest;
import com.dekk.app.admin.presentation.request.AdminSignupRequest;
import com.dekk.app.admin.presentation.response.AdminResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginAdmin;
import com.dekk.global.security.util.ClientIpExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adm/v1/admins")
@RequiredArgsConstructor
public class AdminCommandController implements AdminCommandApi {

    private final AdminCommandService adminCommandService;

    @Override
    @PostMapping("/invite")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> inviteAdmin(
            @Valid @RequestBody AdminInviteRequest request,
            HttpServletRequest httpRequest,
            @LoginAdmin Long inviterId) {
        String clientIp = ClientIpExtractor.extract(httpRequest);
        adminCommandService.inviteAdmin(inviterId, request.toCommand(), clientIp);

        return ResponseEntity.ok(ApiResponse.from(AdminResultCode.ADMIN_INVITED));
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> completeSignup(
            @Valid @RequestBody AdminSignupRequest request, HttpServletRequest httpRequest) {
        String clientIp = ClientIpExtractor.extract(httpRequest);
        adminCommandService.completeSignup(request.toCommand(), clientIp);

        return ResponseEntity.status(AdminResultCode.ADMIN_CREATED.status())
                .body(ApiResponse.from(AdminResultCode.ADMIN_CREATED));
    }

    @Override
    @PostMapping("/{targetAdminId}/suspend")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> suspendAdmin(
            @PathVariable("targetAdminId") Long targetAdminId,
            HttpServletRequest httpRequest,
            @LoginAdmin Long superAdminId) {
        String clientIp = ClientIpExtractor.extract(httpRequest);
        adminCommandService.suspendAdmin(superAdminId, targetAdminId, clientIp);

        return ResponseEntity.ok(ApiResponse.from(AdminResultCode.ADMIN_SUSPENDED));
    }
}
