package com.dekk.domain.user.entity;


import com.dekk.common.error.BusinessException;
import com.dekk.common.error.UserErrorCode;
import com.dekk.domain.user.model.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false, length = 20, unique = true)
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

    public static Profile create(User user, Double height, Double weight, String nickname, Gender gender) {
        return new Profile(user, height, weight, nickname, gender);
    }

    public void update(String nickname, Double height, Double weight, Gender gender) {
        if (nickname != null && !nickname.isBlank()) {
            validateNickname(nickname);
            this.nickname = nickname;
        }
        if (height != null) {
            this.height = height;
        }
        if (weight != null) {
            this.weight = weight;
        }
        if (gender != null) {
            this.gender = gender;
        }
    }
    private void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank() || nickname.length() > 20) {
            throw new BusinessException(UserErrorCode.INVALID_NICKNAME);
        }
    }
}
