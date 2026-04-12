package com.dekk.app.deck.application;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.dekk.app.deck.application.CustomDeckCommandService;
import com.dekk.app.deck.application.DeckWithdrawalCommandService;
import com.dekk.app.deck.application.ShareDeckCommandService;
import com.dekk.app.deck.domain.model.Deck;
import com.dekk.app.deck.domain.repository.DeckCardRepository;
import com.dekk.app.deck.domain.repository.DeckMemberRepository;
import com.dekk.app.deck.domain.repository.DeckRepository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeckWithdrawalCommandServiceTest {

    @InjectMocks
    private DeckWithdrawalCommandService deckWithdrawalCommandService;

    @Mock
    private DeckRepository deckRepository;
    @Mock
    private DeckMemberRepository deckMemberRepository;
    @Mock
    private DeckCardRepository deckCardRepository;
    @Mock
    private CustomDeckCommandService customDeckCommandService;
    @Mock
    private ShareDeckCommandService shareDeckCommandService;

    @Test
    @DisplayName("유저 탈퇴 시 덱 타입에 따라 알맞은 정리 로직이 호출된다")
    void processWithdrawal_routesCorrectly() {
        Long userId = 1L;

        Deck defaultDeck = mock(Deck.class);
        given(defaultDeck.getId()).willReturn(10L);
        given(defaultDeck.isDefault()).willReturn(true);

        Deck customDeck = mock(Deck.class);
        given(customDeck.getId()).willReturn(20L);
        given(customDeck.isCustom()).willReturn(true);

        Deck sharedDeck = mock(Deck.class);
        given(sharedDeck.getId()).willReturn(30L);
        given(sharedDeck.isShared()).willReturn(true);

        given(deckRepository.findAllByUserIdOrderByTypeAndCreatedAtDesc(userId))
            .willReturn(List.of(defaultDeck, customDeck, sharedDeck));

        given(deckRepository.findById(10L)).willReturn(Optional.of(defaultDeck));

        deckWithdrawalCommandService.processWithdrawal(userId);

        verify(deckCardRepository).deleteAllByDeckId(10L);
        verify(deckMemberRepository).deleteAllByDeckId(10L);
        verify(deckRepository).delete(defaultDeck);

        verify(customDeckCommandService).deleteCustomDeck(userId, 20L);

        verify(shareDeckCommandService).withdrawFromSharedDeck(userId, 30L);
    }
}
