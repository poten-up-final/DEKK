package com.dekk.app.admin.presentation.request;

import com.dekk.app.admin.application.dto.command.AdminSignupCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "관리자 가입 완료 요청")
public record AdminSignupRequest(
        @Schema(description = "이메일로 전달받은 1회성 초대 토큰") @NotBlank(message = "초대 토큰은 필수입니다.")
        String token,

        @Schema(description = "설정할 비밀번호 (영문, 숫자, 특수문자 조합 8~20자)", example = "DekkAdmin123!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
        String password,

        @Schema(description = "관리자 소속(회사/부서명)", example = "DEKK 운영팀")
        @NotBlank(message = "소속은 필수입니다.")
        @Size(max = 100, message = "소속은 100자 이내여야 합니다.")
        String department) {
    public AdminSignupCommand toCommand() {
        return new AdminSignupCommand(token, password, department);
    }
}
