package com.dekk.app.admin.infrastructure.redis;

import com.dekk.app.admin.domain.repository.AdminLoginRateLimiter;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminLoginRateLimiterRedisImpl implements AdminLoginRateLimiter {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "LOGIN_FAIL:";
    private static final int MAX_FAIL_COUNT = 5;
    private static final long LOCK_TIME_MINUTES = 30;
    private static final long TRACKING_TIME_MINUTES = 5;

    private String generateKey(String ip, String email) {
        return PREFIX + ip + ":" + email;
    }

    @Override
    public boolean isLocked(String ip, String email) {
        try {
            String countStr = redisTemplate.opsForValue().get(generateKey(ip, email));
            return countStr != null && Integer.parseInt(countStr) >= MAX_FAIL_COUNT;
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 로그인 잠금 상태 조회 실패", e);
            return false;
        }
    }

    @Override
    public void incrementFailCount(String ip, String email) {
        try {
            String key = generateKey(ip, email);
            Long currentCount = redisTemplate.opsForValue().increment(key);

            if (currentCount != null && currentCount >= MAX_FAIL_COUNT) {
                redisTemplate.expire(key, LOCK_TIME_MINUTES, TimeUnit.MINUTES);
                log.warn("[Admin Lockout] 어드민 계정 잠금 처리됨 - IP: {}, Email: {}", ip, email);
            } else if (currentCount != null && currentCount == 1) {
                redisTemplate.expire(key, TRACKING_TIME_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 로그인 실패 카운트 증가 실패", e);
        }
    }

    @Override
    public void resetFailCount(String ip, String email) {
        try {
            redisTemplate.delete(generateKey(ip, email));
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 로그인 실패 카운트 초기화 실패", e);
        }
    }
}
