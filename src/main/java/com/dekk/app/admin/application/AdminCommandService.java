package com.dekk.app.admin.application;

import com.dekk.app.admin.application.dto.command.AdminInviteCommand;
import com.dekk.app.admin.application.dto.command.AdminSignupCommand;
import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRole;
import com.dekk.app.admin.domain.repository.AdminRefreshTokenRepository;
import com.dekk.app.admin.domain.repository.AdminRepository;
import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
        String payload = String.format("%s:%s:%d", command.email(), command.role().name(), inviterId);

        redisTemplate.opsForValue().set(INVITE_PREFIX + token, payload, 24, TimeUnit.HOURS);

        adminEmailService.sendInviteEmail(command.email(), token);

        log.info("[Audit] 관리자 초대 토큰 발행. InviterId: {}, Email: {}, IP: {}", inviterId, command.email(), clientIp);
        log.info("[테스트용 임시 토큰 발급] Token: {}", token);
    }


    public void completeSignup(AdminSignupCommand command, String clientIp) {
        String data = redisTemplate.opsForValue().get(INVITE_PREFIX + command.token());
        if (data == null) {
            throw new AdminBusinessException(AdminErrorCode.INVALID_INVITE_TOKEN);
        }

        String[] parts = data.split(":");
        String email = parts[0];
        AdminRole role = AdminRole.valueOf(parts[1]);

        Admin admin = Admin.create(email, passwordEncoder.encode(command.password()), role, command.department());
        adminRepository.save(admin);

        redisTemplate.delete(INVITE_PREFIX + command.token());
        log.info("[Audit] 신규 관리자 가입 완료. AdminId: {}, IP: {}", admin.getId(), clientIp);
    }

    public void suspendAdmin(Long suspenderId, Long targetId, String clientIp) {
        Admin admin = adminRepository
            .findById(targetId)
            .orElseThrow(() -> new AdminBusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

        admin.suspend();
        adminRefreshTokenRepository.deleteByAdminId(targetId);
        adminTokenBlackListRepository.saveKickOut(targetId, adminAtValidityTime);

        log.warn("[Audit] 관리자 강제 정지(Kill-Switch). TargetId: {}, IP: {}", targetId, clientIp);
    }
}
