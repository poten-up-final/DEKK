package com.dekk.app.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.dekk.app.admin.application.dto.command.AdminSignupCommand;
import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRole;
import com.dekk.app.admin.domain.model.AdminStatus;
import com.dekk.app.admin.domain.repository.AdminRefreshTokenRepository;
import com.dekk.app.admin.domain.repository.AdminRepository;
import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminCommandServiceTest {

    private static final String INVITE_KEY = "ADMIN:INVITE:invite-token";
    private static final String CLIENT_IP = "127.0.0.1";

    @InjectMocks
    private AdminCommandService adminCommandService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminRefreshTokenRepository adminRefreshTokenRepository;

    @Mock
    private AdminTokenBlackListRepository adminTokenBlackListRepository;

    @Mock
    private AdminEmailService adminEmailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminCommandService, "adminAtValidityTime", 3600L);
    }

    @Test
    @DisplayName("관리자 가입 완료 시 초대 토큰을 원자적으로 소비한다")
    void completeSignup_consumesInviteTokenAtomically() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete(INVITE_KEY)).willReturn("new-admin@dekk.com:ADMIN:1");
        given(adminRepository.existsByEmail("new-admin@dekk.com")).willReturn(false);
        given(passwordEncoder.encode("DekkAdmin123!")).willReturn("encoded-password");
        given(adminRepository.saveAndFlush(any(Admin.class))).willAnswer(invocation -> invocation.getArgument(0));

        adminCommandService.completeSignup(
            new AdminSignupCommand("invite-token", "DekkAdmin123!", "운영팀"), CLIENT_IP);

        ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).saveAndFlush(adminCaptor.capture());
        Admin savedAdmin = adminCaptor.getValue();
        assertThat(savedAdmin.getEmail()).isEqualTo("new-admin@dekk.com");
        assertThat(savedAdmin.getAdminRole()).isEqualTo(AdminRole.ADMIN);
        assertThat(savedAdmin.getStatus()).isEqualTo(AdminStatus.ACTIVE);
        assertThat(savedAdmin.getDepartment()).isEqualTo("운영팀");
        verify(valueOperations).getAndDelete(INVITE_KEY);
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("초대 토큰이 없으면 가입 완료에 실패한다")
    void completeSignup_failsWhenInviteTokenIsMissing() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete(INVITE_KEY)).willReturn(null);

        assertThatThrownBy(() -> adminCommandService.completeSignup(
            new AdminSignupCommand("invite-token", "DekkAdmin123!", "운영팀"), CLIENT_IP))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.INVALID_INVITE_TOKEN.message());

        verify(adminRepository, never()).saveAndFlush(any(Admin.class));
    }

    @Test
    @DisplayName("이미 등록된 이메일이면 소비된 초대 토큰으로 가입할 수 없다")
    void completeSignup_failsWhenEmailAlreadyExists() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete(INVITE_KEY)).willReturn("new-admin@dekk.com:ADMIN:1");
        given(adminRepository.existsByEmail("new-admin@dekk.com")).willReturn(true);

        assertThatThrownBy(() -> adminCommandService.completeSignup(
            new AdminSignupCommand("invite-token", "DekkAdmin123!", "운영팀"), CLIENT_IP))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.DUPLICATE_EMAIL.message());

        verify(adminRepository, never()).saveAndFlush(any(Admin.class));
    }

    @Test
    @DisplayName("동시 가입 경쟁으로 unique 제약이 충돌하면 중복 이메일 오류로 변환한다")
    void completeSignup_mapsUniqueConstraintViolationToDuplicateEmail() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete(INVITE_KEY)).willReturn("new-admin@dekk.com:ADMIN:1");
        given(adminRepository.existsByEmail("new-admin@dekk.com")).willReturn(false);
        given(passwordEncoder.encode("DekkAdmin123!")).willReturn("encoded-password");
        given(adminRepository.saveAndFlush(any(Admin.class)))
            .willThrow(new DataIntegrityViolationException("duplicate email"));

        assertThatThrownBy(() -> adminCommandService.completeSignup(
            new AdminSignupCommand("invite-token", "DekkAdmin123!", "운영팀"), CLIENT_IP))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.DUPLICATE_EMAIL.message());
    }

    @Test
    @DisplayName("관리자는 자기 자신을 정지할 수 없다")
    void suspendAdmin_failsWhenTargetIsSelf() {
        assertThatThrownBy(() -> adminCommandService.suspendAdmin(1L, 1L, CLIENT_IP))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.CANNOT_SUSPEND_SELF.message());

        verify(adminRepository, never()).findById(any());
    }

    @Test
    @DisplayName("마지막 활성 슈퍼 관리자는 정지할 수 없다")
    void suspendAdmin_failsWhenTargetIsLastActiveSuperAdmin() {
        Admin target = Admin.create("super@dekk.com", "encoded-password", AdminRole.SUPER_ADMIN, "운영팀");
        given(adminRepository.findById(2L)).willReturn(Optional.of(target));
        given(adminRepository.countByAdminRoleAndStatus(AdminRole.SUPER_ADMIN, AdminStatus.ACTIVE)).willReturn(1L);

        assertThatThrownBy(() -> adminCommandService.suspendAdmin(1L, 2L, CLIENT_IP))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.LAST_SUPER_ADMIN_CANNOT_BE_SUSPENDED.message());

        verify(adminRefreshTokenRepository, never()).deleteByAdminId(any());
        verify(adminTokenBlackListRepository, never()).saveKickOut(any(), anyLong());
    }

    @Test
    @DisplayName("강제 정지 시 kickout 등록에 실패하면 성공 처리하지 않는다")
    void suspendAdmin_failsWhenKickoutRegistrationFails() {
        Admin target = Admin.create("admin@dekk.com", "encoded-password", AdminRole.ADMIN, "운영팀");
        given(adminRepository.findById(2L)).willReturn(Optional.of(target));
        given(adminTokenBlackListRepository.saveKickOut(2L, 3600L)).willReturn(false);

        assertThatThrownBy(() -> adminCommandService.suspendAdmin(1L, 2L, CLIENT_IP))
            .isInstanceOf(AdminBusinessException.class)
            .hasMessageContaining(AdminErrorCode.KICKOUT_REGISTRATION_FAILED.message());

        verify(adminRefreshTokenRepository).deleteByAdminId(2L);
        assertThat(target.getStatus()).isEqualTo(AdminStatus.SUSPENDED);
    }

    @Test
    @DisplayName("관리자 강제 정지에 성공하면 refresh token 삭제와 kickout 등록을 수행한다")
    void suspendAdmin_success() {
        Admin target = Admin.create("admin@dekk.com", "encoded-password", AdminRole.ADMIN, "운영팀");
        given(adminRepository.findById(2L)).willReturn(Optional.of(target));
        given(adminTokenBlackListRepository.saveKickOut(2L, 3600L)).willReturn(true);

        adminCommandService.suspendAdmin(1L, 2L, CLIENT_IP);

        assertThat(target.getStatus()).isEqualTo(AdminStatus.SUSPENDED);
        verify(adminRefreshTokenRepository).deleteByAdminId(2L);
        verify(adminTokenBlackListRepository).saveKickOut(2L, 3600L);
    }
}
