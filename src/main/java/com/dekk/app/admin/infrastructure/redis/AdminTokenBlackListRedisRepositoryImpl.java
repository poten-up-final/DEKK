package com.dekk.app.admin.infrastructure.redis;

import com.dekk.app.admin.domain.repository.AdminTokenBlackListRepository;
import com.dekk.global.security.jwt.TokenBlacklistManager;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

        String key = PREFIX + hashToken(accessToken);
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
        String key = PREFIX + hashToken(accessToken);
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("[Redis Fail-Safe] 어드민 토큰 블랙리스트 조회 실패. 보안을 위해 접근을 차단(Fail-Close)합니다.", e);
            return true;
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
