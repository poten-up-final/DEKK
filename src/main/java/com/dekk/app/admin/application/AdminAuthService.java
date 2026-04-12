package com.dekk.app.admin.application;

import com.dekk.app.admin.application.dto.command.AdminLoginCommand;
import com.dekk.app.admin.application.dto.result.AdminLoginResult;
import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.repository.AdminRepository;
import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
import com.dekk.app.admin.security.AdminUserDetails;
import com.dekk.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminTokenBlackListRepository adminTokenBlackListRepository;

    public AdminLoginResult login(AdminLoginCommand command) {
        Admin admin = adminRepository
                .findByEmail(command.email())
                .orElseThrow(() -> new AdminBusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

        if (!passwordEncoder.matches(command.password(), admin.getPassword())) {
            throw new AdminBusinessException(AdminErrorCode.INVALID_PASSWORD);
        }

        AdminUserDetails userDetails = new AdminUserDetails(
                admin.getId(), admin.getEmail(), admin.getAdminRole().getKey());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        return new AdminLoginResult(accessToken);
    }

    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }

        long ttlSeconds = jwtTokenProvider.getRemainingExpiration(accessToken);
        if (ttlSeconds > 0) {
            adminTokenBlackListRepository.save(accessToken, ttlSeconds);
        }
    }
}
