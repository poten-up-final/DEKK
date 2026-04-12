package com.dekk.app.admin.infrastructure.redis;

import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
import com.dekk.global.security.jwt.TokenBlacklistManager;
import com.dekk.global.security.util.SecurityUtils;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminTokenBlackListRedisRepositoryImpl implements AdminTokenBlackListRepository, TokenBlacklistManager {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "BL:ADMIN:";
    private static final String BLACKLIST_VALUE = "logout";

    @Override
    public void save(String accessToken, long ttlSeconds) {
        if (accessToken == null || accessToken.isBlank() || ttlSeconds <= 0) {
            return;
        }

        String key = PREFIX + SecurityUtils.hashToken(accessToken);
        try {
            redisTemplate.opsForValue().set(key, BLACKLIST_VALUE, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 토큰 블랙리스트 저장 실패", e);
        }
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return true;
        }
        String key = PREFIX + SecurityUtils.hashToken(accessToken);
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 토큰 블랙리스트 조회 실패. 보안을 위해 접근을 차단(Fail-Close)합니다.", e);
            return true;
        }
    }
}
