package com.dekk.app.activelog.domain.repository;

import com.dekk.app.activelog.domain.model.SwipeType;

public interface CardSwipeProjection {
    Long getCardId();

    SwipeType getSwipeType();
}
