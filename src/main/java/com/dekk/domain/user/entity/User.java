package com.dekk.domain.user.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String provider; // "google", "kakao"

    @Column(nullable = false)
    private String providerId; // 소셜 서비스의 고유 아이디(sub 또는 id)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static User createSocialUser(String email, String nickname, Role role, String provider, String providerId) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .role(role)
                .provider(provider)
                .providerId(providerId)
                .build();


    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
