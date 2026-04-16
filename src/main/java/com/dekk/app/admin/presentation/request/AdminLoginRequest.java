package com.dekk.app.admin.presentation.request;

import com.dekk.app.admin.application.dto.command.AdminLoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "관리자 로그인 요청")
public record AdminLoginRequest(
        @Schema(description = "관리자 이메일", example = "admin@dekk.co.kr")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "비밀번호", example = "DekkAdmin123!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
        String password) {
    public AdminLoginCommand toCommand(String ip) {
        return new AdminLoginCommand(email, password, ip);
    }
}
