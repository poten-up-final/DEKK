package com.dekk.app.admin.application;

import com.dekk.app.admin.application.dto.command.AdminLoginCommand;
import com.dekk.app.admin.application.dto.result.AdminLoginResult;
import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRefreshToken;
import com.dekk.app.admin.domain.model.AdminStatus;
import com.dekk.app.admin.domain.repository.AdminLoginRateLimiter;
import com.dekk.app.admin.domain.repository.AdminRefreshTokenRepository;
import com.dekk.app.admin.domain.repository.AdminRepository;
import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
import com.dekk.app.admin.security.AdminUserDetails;
import com.dekk.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminTokenBlackListRepository adminTokenBlackListRepository;
    private final AdminRefreshTokenRepository adminRefreshTokenRepository;
    private final AdminLoginRateLimiter adminLoginRateLimiter;

    @Transactional
    public AdminLoginResult login(AdminLoginCommand command) {

        if (adminLoginRateLimiter.isLocked(command.ip(), command.email())) {
            log.warn("[Security Alert] 잠긴 계정 로그인 시도 차단 - IP: {}, Email: {}", command.ip(), command.email());
            throw new AdminBusinessException(AdminErrorCode.ACCOUNT_LOCKED);
        }

        Admin admin = adminRepository.findByEmail(command.email()).orElseThrow(() -> {
            adminLoginRateLimiter.incrementFailCount(command.ip(), command.email());
            return new AdminBusinessException(AdminErrorCode.ADMIN_NOT_FOUND);
        });

        if (!passwordEncoder.matches(command.password(), admin.getPassword())) {
            adminLoginRateLimiter.incrementFailCount(command.ip(), command.email());
            throw new AdminBusinessException(AdminErrorCode.INVALID_PASSWORD);
        }

        if (admin.getStatus() == AdminStatus.SUSPENDED) {
            throw new AdminBusinessException(AdminErrorCode.ACCOUNT_SUSPENDED);
        }

        adminLoginRateLimiter.resetFailCount(command.ip(), command.email());

        adminRefreshTokenRepository.deleteByAdminId(admin.getId());

        AdminUserDetails userDetails = new AdminUserDetails(
                admin.getId(), admin.getEmail(), admin.getAdminRole().getKey());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtTokenProvider.createAccessToken(auth);
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        adminRefreshTokenRepository.save(AdminRefreshToken.create(admin.getId(), refreshToken));

        log.info("[Admin Login] AdminId: {} 로그인 성공.", admin.getId());
        return new AdminLoginResult(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String accessToken, Long adminId) {
        if (accessToken != null && !accessToken.isBlank()) {
            long ttlSeconds = jwtTokenProvider.getRemainingExpiration(accessToken);
            if (ttlSeconds > 0) {
                adminTokenBlackListRepository.save(accessToken, ttlSeconds);
            }
        }
        if (adminId != null) {
            adminRefreshTokenRepository.deleteByAdminId(adminId);
        }
    }
}
