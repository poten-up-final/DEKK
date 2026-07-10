package com.dekk.app.admin.application;

import com.dekk.app.admin.application.dto.command.AdminInviteCommand;
import com.dekk.app.admin.application.dto.command.AdminSignupCommand;
import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRole;
import com.dekk.app.admin.domain.model.AdminStatus;
import com.dekk.app.admin.domain.repository.AdminRefreshTokenRepository;
import com.dekk.app.admin.domain.repository.AdminRepository;
import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminCommandService {

    private static final String INVITE_PREFIX = "ADMIN:INVITE:";

    private final RedisTemplate<String, String> redisTemplate;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRefreshTokenRepository adminRefreshTokenRepository;
    private final AdminTokenBlackListRepository adminTokenBlackListRepository;
    private final AdminEmailService adminEmailService;

    @Value("${jwt.admin-access-token-validity-in-seconds}")
    private long adminAtValidityTime;

    public void inviteAdmin(Long inviterId, AdminInviteCommand command, String clientIp) {
        if (adminRepository.existsByEmail(command.email())) {
            throw new AdminBusinessException(AdminErrorCode.DUPLICATE_EMAIL);
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        String payload =
                String.format("%s:%s:%d", command.email(), command.role().name(), inviterId);

        redisTemplate.opsForValue().set(INVITE_PREFIX + token, payload, 24, TimeUnit.HOURS);

        adminEmailService.sendInviteEmail(command.email(), token);

        log.info("[Audit] 관리자 초대 토큰 발행. InviterId: {}, Email: {}, IP: {}", inviterId, command.email(), clientIp);
    }

    public void completeSignup(AdminSignupCommand command, String clientIp) {
        String data = redisTemplate.opsForValue().getAndDelete(INVITE_PREFIX + command.token());
        if (data == null) {
            throw new AdminBusinessException(AdminErrorCode.INVALID_INVITE_TOKEN);
        }

        String[] parts = data.split(":");
        if (parts.length != 3) {
            throw new AdminBusinessException(AdminErrorCode.INVALID_INVITE_TOKEN);
        }

        String email = parts[0];
        AdminRole role;
        try {
            role = AdminRole.valueOf(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new AdminBusinessException(AdminErrorCode.INVALID_INVITE_TOKEN);
        }

        if (adminRepository.existsByEmail(email)) {
            throw new AdminBusinessException(AdminErrorCode.DUPLICATE_EMAIL);
        }

        Admin admin = Admin.create(email, passwordEncoder.encode(command.password()), role, command.department());
        try {
            adminRepository.saveAndFlush(admin);
        } catch (DataIntegrityViolationException e) {
            throw new AdminBusinessException(AdminErrorCode.DUPLICATE_EMAIL);
        }

        log.info("[Audit] 신규 관리자 가입 완료. AdminId: {}, IP: {}", admin.getId(), clientIp);
    }

    public void suspendAdmin(Long suspenderId, Long targetId, String clientIp) {
        if (targetId != null && targetId.equals(suspenderId)) {
            throw new AdminBusinessException(AdminErrorCode.CANNOT_SUSPEND_SELF);
        }

        Admin admin = adminRepository
                .findById(targetId)
                .orElseThrow(() -> new AdminBusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

        if (isLastActiveSuperAdmin(admin)) {
            throw new AdminBusinessException(AdminErrorCode.LAST_SUPER_ADMIN_CANNOT_BE_SUSPENDED);
        }

        admin.suspend();
        adminRefreshTokenRepository.deleteByAdminId(targetId);
        if (!adminTokenBlackListRepository.saveKickOut(targetId, adminAtValidityTime)) {
            throw new AdminBusinessException(AdminErrorCode.KICKOUT_REGISTRATION_FAILED);
        }

        log.warn(
                "[Audit] 관리자 강제 정지(Kill-Switch). SuspenderId: {}, TargetId: {}, IP: {}",
                suspenderId,
                targetId,
                clientIp);
    }

    private boolean isLastActiveSuperAdmin(Admin admin) {
        return admin.getAdminRole() == AdminRole.SUPER_ADMIN
                && admin.getStatus() == AdminStatus.ACTIVE
                && adminRepository.countByAdminRoleAndStatus(AdminRole.SUPER_ADMIN, AdminStatus.ACTIVE) <= 1;
    }
}
