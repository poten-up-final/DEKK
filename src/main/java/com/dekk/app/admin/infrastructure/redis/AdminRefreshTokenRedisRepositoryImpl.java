package com.dekk.app.admin.infrastructure.redis;

import com.dekk.app.admin.domain.model.AdminRefreshToken;
import com.dekk.app.admin.domain.repository.AdminRefreshTokenRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminRefreshTokenRedisRepositoryImpl implements AdminRefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "RT:ADMIN:";

    @Value("${jwt.admin-refresh-token-validity-in-seconds}")
    private long refreshTokenTtlSeconds;

    @Override
    public void save(AdminRefreshToken refreshToken) {
        String key = PREFIX + refreshToken.getAdminId();
        try {
            redisTemplate.opsForValue().set(key, refreshToken.getToken(), refreshTokenTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 Refresh Token 저장 실패 - AdminId: {}", refreshToken.getAdminId(), e);
        }
    }

    @Override
    public void deleteByAdminId(Long adminId) {
        String key = PREFIX + adminId;
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 Refresh Token 삭제 실패 - AdminId: {}", adminId, e);
        }
    }
}
