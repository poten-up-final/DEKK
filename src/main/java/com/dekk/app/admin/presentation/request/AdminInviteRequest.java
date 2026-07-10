package com.dekk.app.admin.presentation.request;

import com.dekk.app.admin.application.dto.command.AdminInviteCommand;
import com.dekk.app.admin.domain.model.AdminRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리자 초대 요청")
public record AdminInviteRequest(
        @Schema(description = "초대할 관리자 이메일", example = "new_admin@dekk.co.kr")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "부여할 권한 (ADMIN 또는 SUPER_ADMIN)", example = "ADMIN")
        @NotNull(message = "관리자 권한(Role)은 필수입니다.")
        AdminRole role) {
    public AdminInviteCommand toCommand() {
        return new AdminInviteCommand(email, role);
    }
}
