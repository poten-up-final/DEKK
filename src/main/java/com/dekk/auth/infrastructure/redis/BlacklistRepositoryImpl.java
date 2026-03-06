package com.dekk.auth.infrastructure.redis;

import com.dekk.auth.domain.model.BlacklistAccessToken;
import com.dekk.auth.domain.repository.BlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class BlacklistRepositoryImpl implements BlacklistRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "BL:";

    @Override
    public void save(BlacklistAccessToken blacklistAccessToken, long remainingSeconds) {
        String key = PREFIX + blacklistAccessToken.getAccessToken();
        redisTemplate.opsForValue().set(key, blacklistAccessToken.getStatus(), remainingSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean existsByAccessToken(String accessToken) {
        String key = PREFIX + accessToken;
        return redisTemplate.hasKey(key);

    }
}
