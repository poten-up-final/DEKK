package com.dekk.app.user.application.dto.result;

import com.dekk.app.user.domain.model.User;
import com.dekk.app.user.domain.model.enums.UserStatus;

public record UserAuthResult(Long id, String email, String role, UserStatus status) {
    public static UserAuthResult from(User user) {
        return new UserAuthResult(user.getId(), user.getEmail(), user.getRole().getKey(), user.getStatus());
    }
}
