package com.dekk.app.deck.application.handler;

import com.dekk.app.deck.application.DeckWithdrawalCommandService;
import com.dekk.app.deck.application.DefaultDeckCommandService;
import com.dekk.global.event.UserDeletedEvent;
import com.dekk.global.event.UserOnboardedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserEventHandler {

    private final DefaultDeckCommandService defaultDeckCommandService;
    private final DeckWithdrawalCommandService deckWithdrawalCommandService;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleUserOnboarded(UserOnboardedEvent event) {
        defaultDeckCommandService.createDefaultDeck(event.userId());
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleUserDeleted(UserDeletedEvent event) {
        deckWithdrawalCommandService.processWithdrawal(event.userId());
    }
}
