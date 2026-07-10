package com.dekk.app.admin.infrastructure.redis;

import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
import com.dekk.global.security.util.SecurityUtils;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminTokenBlackListRedisRepositoryImpl implements AdminTokenBlackListRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String AT_PREFIX = "BL:ADMIN:";
    private static final String KICKOUT_PREFIX = "BL:ADMIN_KICK:";

    @Override
    public void save(String accessToken, long ttlSeconds) {
        if (accessToken == null || accessToken.isBlank() || ttlSeconds <= 0) return;
        String key = AT_PREFIX + SecurityUtils.hashToken(accessToken);
        try {
            redisTemplate.opsForValue().set(key, "logout", ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 토큰 블랙리스트 저장 실패", e);
        }
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) return true;
        String key = AT_PREFIX + SecurityUtils.hashToken(accessToken);
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 블랙리스트 조회 실패 (Fail-Close)", e);
            return true;
        }
    }

    @Override
    public boolean saveKickOut(Long adminId, long ttlSeconds) {
        if (adminId == null || ttlSeconds <= 0) return false;
        try {
            redisTemplate.opsForValue().set(KICKOUT_PREFIX + adminId, "kicked_out", ttlSeconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 강제 킥아웃 수배령 저장 실패 - AdminId: {}", adminId, e);
            return false;
        }
    }

    @Override
    public boolean isKickedOut(Long adminId) {
        if (adminId == null) return true;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(KICKOUT_PREFIX + adminId));
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 강제 킥아웃 수배령 조회 실패 (Fail-Close) - AdminId: {}", adminId, e);
            return true;
        }
    }
}
