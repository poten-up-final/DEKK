package com.dekk.app.admin.domain.repository;

import com.dekk.app.admin.domain.model.AdminRefreshToken;

public interface AdminRefreshTokenRepository {
    void save(AdminRefreshToken refreshToken);

    void deleteByAdminId(Long adminId);
}
