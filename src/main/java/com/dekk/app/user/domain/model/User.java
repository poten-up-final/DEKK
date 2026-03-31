package com.dekk.app.user.domain.model;

import com.dekk.app.user.application.command.UserCreateCommand;
import com.dekk.app.user.application.command.UserOnboardingCommand;
import com.dekk.app.user.application.command.UserProfileUpdateCommand;
import com.dekk.app.user.domain.exception.UserBusinessException;
import com.dekk.app.user.domain.exception.UserErrorCode;
import com.dekk.app.user.domain.model.enums.Provider;
import com.dekk.app.user.domain.model.enums.Role;
import com.dekk.app.user.domain.model.enums.UserStatus;
import com.dekk.global.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "users",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_user_email_provider",
                    columnNames = {"email", "provider"})
        })
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Profile profile;

    private User(String email, Provider provider, String providerId) {
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.status = UserStatus.PENDING;
        this.role = Role.MEMBER;
    }

    public static User create(UserCreateCommand command) {
        return new User(command.email(), command.provider(), command.providerId());
    }

    public void completeOnboarding(UserOnboardingCommand command) {
        if (this.status != UserStatus.PENDING) {
            throw new UserBusinessException(UserErrorCode.ALREADY_ONBOARDED);
        }
        this.status = UserStatus.ACTIVE;
        this.profile = Profile.create(this, command);
    }

    public void updateProfileInfo(UserProfileUpdateCommand command) {
        if (this.profile != null) {
            this.profile.update(command);
        }
    }

    public void deleteUser() {
        this.status = UserStatus.DELETED;
        this.markAsDeleted();
    }
}
