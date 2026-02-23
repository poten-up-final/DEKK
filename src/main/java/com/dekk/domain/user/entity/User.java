package com.dekk.domain.user.entity;

import com.dekk.domain.user.model.Gender;
import com.dekk.domain.user.model.Provider;
import com.dekk.domain.user.model.Role;
import com.dekk.domain.user.model.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.dekk.global.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users") // DB의 예약어 충돌 방지를 위해 주로 복수형을 사용합니다.
public class User extends BaseTimeEntity {

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
    private Provider provider;

    @Column(nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private Double height;
    private Double weight;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public static User createSocialUser(String email, Provider provider, String providerId) {
        User user = new User();
        user.email = email;
        user.provider = provider;
        user.providerId = providerId;
        user.status = UserStatus.PENDING;
        user.role = Role.MEMBER;
        return user;
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
        this.markAsDeleted();
    }
}