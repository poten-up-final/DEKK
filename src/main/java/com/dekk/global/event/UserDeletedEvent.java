package com.dekk.global.event;

public record UserDeletedEvent(Long userId) {
    public static UserDeletedEvent of(Long userId) {
        return new UserDeletedEvent(userId);
    }
}
