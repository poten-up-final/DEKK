package com.dekk.activelog.application;

import com.dekk.activelog.application.dto.command.SwipeCommand;
import com.dekk.activelog.domain.exception.ActiveLogBusinessException;
import com.dekk.activelog.domain.exception.ActiveLogErrorCode;
import com.dekk.activelog.domain.model.ActiveLog;
import com.dekk.activelog.domain.repository.ActiveLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActiveLogCommandService {

    private final ActiveLogRepository activeLogRepository;

    @Transactional
    public void saveSwipeAction(SwipeCommand command) {
        if (activeLogRepository.existsByUserIdAndCardId(command.userId(), command.cardId())) {
            return;
        }

        ActiveLog activeLog = ActiveLog.create(command.userId(), command.cardId(), command.swipeType());
        activeLogRepository.save(activeLog);
    }
}
