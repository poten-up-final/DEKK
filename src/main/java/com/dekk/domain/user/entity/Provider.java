package com.dekk.domain.user.entity;

import com.dekk.domain.auth.dto.GoogleOAuth2UserInfo;
import com.dekk.domain.auth.dto.KakaoOAuth2UserInfo;
import com.dekk.domain.auth.dto.OAuth2UserInfo;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public enum Provider {
    GOOGLE("google"), KAKAO("kakao");

    private final String registrationId;

    Provider(String registrationId) {
        this.registrationId = registrationId;
    }

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "kakao" -> new KakaoOAuth2UserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인: " + registrationId);
        };
    }
}