package com.dekk.app.admin.infrastructure.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class AdminTokenBlackListRedisRepositoryImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AdminTokenBlackListRedisRepositoryImpl repository;

    private static final String DUMMY_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.dummy";
    private static final String HASHED_KEY_PREFIX = "BL:ADMIN:";

    @Test
    @DisplayName("토큰 저장 시 SHA-256으로 해싱된 키를 사용하여 Redis에 저장한다")
    void save_success() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        repository.save(DUMMY_TOKEN, 3600L);

        verify(valueOperations).set(
            org.mockito.ArgumentMatchers.argThat(key -> key.startsWith(HASHED_KEY_PREFIX) && !key.contains(DUMMY_TOKEN)),
            eq("logout"),
            eq(3600L),
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("정상적인 상황에서 블랙리스트에 토큰이 존재하면 true를 반환한다")
    void isBlacklisted_true_when_exists() {
        given(redisTemplate.hasKey(anyString())).willReturn(true);

        boolean result = repository.isBlacklisted(DUMMY_TOKEN);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Redis 타임아웃 및 장애 발생 시 Fail-Close 정책에 따라 보안을 위해 true를 반환한다")
    void isBlacklisted_failClose_when_redis_down() {
        given(redisTemplate.hasKey(anyString())).willThrow(new RedisConnectionFailureException("Redis Down"));

        boolean result = repository.isBlacklisted(DUMMY_TOKEN);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("토큰이 null이거나 공백이면 보안을 위해 즉시 true를 반환한다")
    void isBlacklisted_true_when_token_is_blank() {
        assertThat(repository.isBlacklisted(null)).isTrue();
        assertThat(repository.isBlacklisted("   ")).isTrue();
    }
}
