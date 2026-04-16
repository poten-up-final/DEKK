package com.dekk.app.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.dekk.app.admin.application.dto.command.AdminLoginCommand;
import com.dekk.app.admin.application.dto.result.AdminLoginResult;
import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRole;
import com.dekk.app.admin.domain.model.AdminStatus;
import com.dekk.app.admin.domain.repository.AdminLoginRateLimiter;
import com.dekk.app.admin.domain.repository.AdminRefreshTokenRepository;
import com.dekk.app.admin.domain.repository.AdminRepository;
import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
import com.dekk.global.security.jwt.JwtTokenProvider;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private AdminTokenBlackListRepository adminTokenBlackListRepository;

    @Mock
    private AdminRefreshTokenRepository adminRefreshTokenRepository;

    @Mock
    private AdminLoginRateLimiter adminLoginRateLimiter;

    @Test
    @DisplayName("관리자 로그인 성공")
    void login_success() {
        // given
        String email = "admin@dekk.com";
        String password = "password";
        String ip = "127.0.0.1";
        AdminLoginCommand command = new AdminLoginCommand(email, password, ip);
        Admin admin = mock(Admin.class);

        given(adminLoginRateLimiter.isLocked(ip, email)).willReturn(false);
        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(admin.getPassword()).willReturn("encodedPassword");
        given(passwordEncoder.matches(password, "encodedPassword")).willReturn(true);
        given(admin.getStatus()).willReturn(AdminStatus.ACTIVE);
        given(admin.getId()).willReturn(1L);
        given(admin.getEmail()).willReturn(email);
        given(admin.getAdminRole()).willReturn(AdminRole.ADMIN);

        given(jwtTokenProvider.createAccessToken(any(Authentication.class))).willReturn("test-access-token");
        given(jwtTokenProvider.createRefreshToken(any(Authentication.class))).willReturn("test-refresh-token");

        AdminLoginResult result = adminAuthService.login(command);

        assertThat(result.accessToken()).isEqualTo("test-access-token");
        assertThat(result.refreshToken()).isEqualTo("test-refresh-token");
        verify(adminLoginRateLimiter).resetFailCount(ip, email);
        verify(adminRefreshTokenRepository).deleteByAdminId(1L);
    }

    @Test
    @DisplayName("관리자 로그인 실패 - 존재하지 않는 이메일")
    void login_fail_not_found_email() {
        String email = "notfound@dekk.com";
        String ip = "127.0.0.1";
        AdminLoginCommand command = new AdminLoginCommand(email, "password", ip);

        given(adminLoginRateLimiter.isLocked(ip, email)).willReturn(false);
        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminAuthService.login(command))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.ADMIN_NOT_FOUND.message());

        verify(adminLoginRateLimiter).incrementFailCount(ip, email);
    }

    @Test
    @DisplayName("관리자 로그인 실패 - 비밀번호 불일치")
    void login_fail_invalid_password() {
        String email = "admin@dekk.com";
        String password = "wrongpassword";
        String ip = "127.0.0.1";
        AdminLoginCommand command = new AdminLoginCommand(email, password, ip);
        Admin admin = mock(Admin.class);

        given(adminLoginRateLimiter.isLocked(ip, email)).willReturn(false);
        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(admin.getPassword()).willReturn("encodedPassword");
        given(passwordEncoder.matches(password, "encodedPassword")).willReturn(false);

        assertThatThrownBy(() -> adminAuthService.login(command))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.INVALID_PASSWORD.message());

        verify(adminLoginRateLimiter).incrementFailCount(ip, email);
    }

    @Test
    @DisplayName("관리자 로그인 실패 - 계정 잠금 상태")
    void login_fail_account_locked() {
        String email = "admin@dekk.com";
        String ip = "127.0.0.1";
        AdminLoginCommand command = new AdminLoginCommand(email, "password", ip);

        given(adminLoginRateLimiter.isLocked(ip, email)).willReturn(true);

        assertThatThrownBy(() -> adminAuthService.login(command))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.ACCOUNT_LOCKED.message());
    }
}
