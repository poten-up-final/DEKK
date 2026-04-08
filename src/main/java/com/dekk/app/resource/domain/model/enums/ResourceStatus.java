package com.dekk.app.resource.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourceStatus {
    PENDING("업로드 대기 중"),
    PROCESSED("처리 완료"),
    FAILED("처리 실패"),
    EXPIRED("만료됨");

    private final String description;

    public boolean isProcessed() {
        return this == PROCESSED;
    }

    public boolean canBeUsed() {
        return this == PROCESSED;
    }
}
