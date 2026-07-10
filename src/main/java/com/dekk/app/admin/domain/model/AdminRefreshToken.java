package com.dekk.app.admin.domain.model;

import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminRefreshToken {
    private Long adminId;
    private String token;

    private AdminRefreshToken(Long adminId, String token) {
        this.adminId = adminId;
        this.token = token;
    }

    public static AdminRefreshToken create(Long adminId, String token) {
        if (adminId == null || token == null || token.isBlank()) {
            throw new AdminBusinessException(AdminErrorCode.INVALID_TOKEN);
        }
        return new AdminRefreshToken(adminId, token);
    }
}
