package com.dekk.domain.user.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider; // "google", "kakao"

    @Column(nullable = false)
    private String providerId; // 소셜 서비스의 고유 아이디(sub 또는 id)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private Double height;
    private Double weight;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
    private User(String email, Provider provider, String providerId, UserStatus status) {
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.status = status;
        this.role = Role.MEMBER;
    }
    public static User createSocialUser(String email, Provider provider, String providerId) {
        return new User(email, provider, providerId, UserStatus.PENDING);
    }

    public void completeOnboarding(String nickname, Double height, Double weight, Gender gender) {
        this.nickname = nickname;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.status = UserStatus.ACTIVE;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateBodyInfo(Double height, Double weight, Gender gender) {
        this.height = height;
        this.weight = weight;
        this.gender = gender;
    }

    public void deleteUser() {
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}