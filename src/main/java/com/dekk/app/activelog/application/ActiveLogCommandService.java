package com.dekk.app.activelog.application;

import com.dekk.app.activelog.application.dto.command.SwipeCommand;
import com.dekk.app.activelog.domain.model.ActiveLog;
import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.activelog.domain.repository.ActiveLogRepository;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.deck.application.DeckCardCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ActiveLogCommandService {

    private final ActiveLogRepository activeLogRepository;
    private final DeckCardCommandService deckCardCommandService;
    private final CardQueryService cardQueryService;

    public void saveSwipeAction(SwipeCommand command) {
        Long cardId = cardQueryService.getCardIdByPublicId(command.cardPublicId());

        if (activeLogRepository.existsByUserIdAndCardId(command.userId(), cardId)) {
            return;
        }

        ActiveLog activeLog = ActiveLog.create(command.userId(), cardId, command.swipeType());
        activeLogRepository.save(activeLog);

        if (command.swipeType() == SwipeType.LIKE) {
            deckCardCommandService.saveToDefaultDeck(command.userId(), cardId);
        }
    }
}
