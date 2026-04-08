package com.dekk.app.admin.infrastructure.redis;

import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
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

    private static final String PREFIX = "BL:ADMIN:";
    private static final String BLACKLIST_VALUE = "logout";

    @Override
    public void save(String accessToken, long ttlSeconds) {
        if (ttlSeconds <= 0) {
            return;
        }

        String key = PREFIX + accessToken;
        try {
            redisTemplate.opsForValue().set(key, BLACKLIST_VALUE, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 토큰 블랙리스트 저장 실패", e);
        }
    }

    @Override
    public boolean isBlackListed(String accessToken) {
        String key = PREFIX + accessToken;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 토큰 블랙리스트 조회 실패", e);
            return false;
        }
    }
}
