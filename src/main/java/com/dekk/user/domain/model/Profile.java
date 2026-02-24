package com.dekk.user.domain.model;

import com.dekk.user.application.command.UserOnboardingCommand;
import com.dekk.user.application.command.UserProfileUpdateCommand;
import com.dekk.user.domain.exception.UserBusinessException;
import com.dekk.user.domain.exception.UserErrorCode;
import com.dekk.user.domain.model.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile {

    // 닉네임 정규표현식 (한글, 영문, 숫자, 언더바(_) 허용)
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣_]+$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double height;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false, length = 10, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Profile(User user, Double height, Double weight, String nickname, Gender gender) {
        validateNickname(nickname);
        this.user = user;
        this.height = height;
        this.weight = weight;
        this.nickname = nickname;
        this.gender = gender;
    }

    public static Profile create(User user, UserOnboardingCommand command) {
        return new Profile(user, command.height(), command.weight(), command.nickname(), command.gender());
    }

    public void update(UserProfileUpdateCommand command) {
        if (command.nickname() != null && !command.nickname().isBlank()) {
            validateNickname(command.nickname());
            this.nickname = command.nickname();
        }
        if (command.height() != null) {
            this.height = command.height();
        }
        if (command.weight() != null) {
            this.weight = command.weight();
        }
        if (command.gender() != null) {
            this.gender = command.gender();
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank() || nickname.length() > 10) {
            throw new UserBusinessException(UserErrorCode.INVALID_NICKNAME);
        }

        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new UserBusinessException(UserErrorCode.INVALID_NICKNAME);
        }
    }
}
