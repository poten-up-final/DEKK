package com.dekk.domain.user.entity;


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

    private Profile(User user, Double height, Double weight, String nickname) {
        this.user = user;
        this.height = height;
        this.weight = weight;
        this.nickname = nickname;
    }

    public static Profile create(User user, Double height, Double weight, String nickname) {
        return new Profile(user, height, weight, nickname);
    }

    public void update(String nickname, Double height, Double weight) {
            if (nickname != null && !nickname.isBlank()) {
                this.nickname = nickname;
            }
            if (height != null) {
                this.height = height;
            }
            if (weight != null) {
                this.weight = weight;
        }
    }
}
