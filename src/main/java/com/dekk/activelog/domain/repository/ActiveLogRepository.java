package com.dekk.activelog.domain.repository;

import com.dekk.activelog.domain.model.ActiveLog;
import java.util.Optional;

public interface ActiveLogRepository {
    ActiveLog save(ActiveLog activeLog);

    // 동시성 방어 및 중복 스와이프 검증용
    boolean existsByUserIdAndCardId(Long userId, Long cardId);

    // 보관함 취소 시 이력 초기화(Soft Delete)를 위한 조회 및 삭제
    Optional<ActiveLog> findByUserIdAndCardId(Long userId, Long cardId);
    void delete(ActiveLog activeLog);
}
