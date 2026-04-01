package com.dekk.global.event;

public record UserOnboardedEvent(Long userId) {
    public static UserOnboardedEvent of(Long userId) {
        return new UserOnboardedEvent(userId);
    }
}
