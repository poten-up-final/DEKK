package com.dekk.auth.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    private Long userId;
    private String token;

    private RefreshToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public static RefreshToken create(Long userId, String token) {
        if(userId == null || token == null || token.isBlank()) {
            throw new IllegalArgumentException("UserId와 Token은 필수입니다.");
        }
        return new RefreshToken(userId, token);
    }
}
