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
    private static final String KICKOUT_KEY_PREFIX = "BL:ADMIN_KICK:";

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

    @Test
    @DisplayName("강제 킥아웃 등록에 성공하면 true를 반환한다")
    void saveKickOut_success() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        boolean result = repository.saveKickOut(1L, 3600L);

        assertThat(result).isTrue();
        verify(valueOperations).set(
                eq(KICKOUT_KEY_PREFIX + 1L), eq("kicked_out"), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("강제 킥아웃 등록 중 Redis 장애가 발생하면 false를 반환한다")
    void saveKickOut_false_when_redis_down() {
        given(redisTemplate.opsForValue()).willThrow(new RedisConnectionFailureException("Redis Down"));

        boolean result = repository.saveKickOut(1L, 3600L);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("강제 킥아웃 등록 입력값이 유효하지 않으면 false를 반환한다")
    void saveKickOut_false_when_invalid_input() {
        assertThat(repository.saveKickOut(null, 3600L)).isFalse();
        assertThat(repository.saveKickOut(1L, 0L)).isFalse();
    }
}
