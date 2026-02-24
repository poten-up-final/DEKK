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
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

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

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

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
        validatePendingStatus();
        this.gender = gender;
        this.status = UserStatus.ACTIVE;
        this.profile = Profile.create(this, height, weight, nickname);
    }

    public void updateProfileInfo(String nickname, Double height, Double weight, Gender gender) {
        if(this.profile != null) {
            this.profile.update(nickname, height, weight);
        }
        if(gender != null) {
            this.gender = gender;
        }
    }

    public void deleteUser() {
        this.status = UserStatus.DELETED;
        this.markAsDeleted();
    }

    private void validatePendingStatus() {
        if (this.status != UserStatus.PENDING) {
            throw new IllegalStateException("이미 온보딩이 완료된 사용자입니다.");
        }
    }
}