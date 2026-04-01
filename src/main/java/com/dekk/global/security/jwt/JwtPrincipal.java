package com.dekk.global.security.jwt;

import com.dekk.app.user.domain.model.enums.UserStatus;

public interface JwtPrincipal {
    Long getJwtId();

    String getJwtEmail();

    String getJwtRole();

    UserStatus getJwtStatus();
}
