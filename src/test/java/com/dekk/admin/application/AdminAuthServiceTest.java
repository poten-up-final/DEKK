package com.dekk.admin.application;

import com.dekk.app.admin.application.AdminAuthService;
import com.dekk.app.admin.application.dto.command.AdminLoginCommand;
import com.dekk.app.admin.application.dto.result.AdminLoginResult;
import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRole;
import com.dekk.app.admin.domain.repository.AdminRepository;
import com.dekk.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @InjectMocks
    private AdminAuthService adminAuthService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 로그인 성공")
    void login_success() {
        // given
        String email = "admin@dekk.com";
        String password = "password";
        AdminLoginCommand command = new AdminLoginCommand(email, password);
        Admin admin = mock(Admin.class);

        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(admin.getPassword()).willReturn("encodedPassword");
        given(passwordEncoder.matches(password, "encodedPassword")).willReturn(true);
        given(admin.getId()).willReturn(1L);
        given(admin.getEmail()).willReturn(email);
        given(admin.getAdminRole()).willReturn(AdminRole.ADMIN);

        given(jwtTokenProvider.createAccessToken(any(Authentication.class))).willReturn("test-token");

        // when
        AdminLoginResult result = adminAuthService.login(command);

        // then
        assertThat(result.accessToken()).isEqualTo("test-token");
    }

    @Test
    @DisplayName("관리자 로그인 실패 - 존재하지 않는 이메일")
    void login_fail_not_found_email() {
        String email = "notfound@dekk.com";
        AdminLoginCommand command = new AdminLoginCommand(email, "password");

        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminAuthService.login(command))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.ADMIN_NOT_FOUND.message());
    }

    @Test
    @DisplayName("관리자 로그인 실패 - 비밀번호 불일치")
    void login_fail_invalid_password() {
        String email = "admin@dekk.com";
        String password = "wrongpassword";
        AdminLoginCommand command = new AdminLoginCommand(email, password);
        Admin admin = mock(Admin.class);

        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(admin.getPassword()).willReturn("encodedPassword");
        given(passwordEncoder.matches(password, "encodedPassword")).willReturn(false);

        assertThatThrownBy(() -> adminAuthService.login(command))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.INVALID_PASSWORD.message());
    }
}
