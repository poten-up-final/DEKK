package com.dekk.auth.infrastructure.redis;

import com.dekk.auth.domain.model.RefreshToken;
import com.dekk.auth.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "RT:";

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenTtlSeconds;

    @Override
    public void save(RefreshToken refreshToken) {
        String key = PREFIX + refreshToken.getUserId();
        redisTemplate.opsForValue().set(key, refreshToken.getToken(), refreshTokenTtlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        String key = PREFIX + userId;
        String token = redisTemplate.opsForValue().get(key);
        if (token == null) {
            return Optional.empty();
        }
        return Optional.of(RefreshToken.create(userId, token));
    }

    @Override
    public void deleteByUserId(Long userId) {
        String key = PREFIX + userId;
        redisTemplate.delete(key);

    }
}
