package com.dekk.app.admin.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminRole {
    SUPER_ADMIN("ROLE_SUPER_ADMIN", "슈퍼 관리자 권한"),
    ADMIN("ROLE_ADMIN", "관리자 권한");

    private final String key;
    private final String title;
}
