package com.dekk.app.deck.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.dekk.app.deck.application.DeckCardCommandService;
import com.dekk.app.deck.domain.exception.DeckBusinessException;
import com.dekk.app.deck.domain.exception.DeckErrorCode;
import com.dekk.app.deck.domain.model.Deck;
import com.dekk.app.deck.domain.repository.DeckCardRepository;
import com.dekk.app.deck.domain.repository.DeckRepository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeckCardCommandServiceTest {

    @InjectMocks
    private DeckCardCommandService deckCardCommandService;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private DeckCardRepository deckCardRepository;

    private final Long userId = 1L;
    private final Long customDeckId = 10L;
    private final Long cardId = 100L;

    @Test
    @DisplayName("커스텀 보관함에 동일한 카드를 저장하려 하면 예외 없이 통과(Return)하고 추가 저장하지 않는다")
    void saveToCustomDeck_returnsEarly_whenCardDuplicated() {
        // given
        Deck customDeck = Deck.createCustom(userId, "여름 코디");
        given(deckRepository.findByIdAndMemberUserId(customDeckId, userId))
            .willReturn(Optional.of(customDeck));

        given(deckCardRepository.existsByDeckIdAndCardId(any(), anyLong())).willReturn(true);

        deckCardCommandService.saveToCustomDeck(userId, customDeckId, cardId);

        verify(deckCardRepository, never()).save(any());
    }

    @Test
    @DisplayName("커스텀 보관함에 50장이 가득 찬 상태에서 저장하려 하면 예외가 발생한다")
    void saveToCustomDeck_throwsException_whenLimitExceeded() {
        Deck customDeck = Deck.createCustom(userId, "여름 코디");
        given(deckRepository.findByIdAndMemberUserId(customDeckId, userId))
            .willReturn(Optional.of(customDeck));

        given(deckCardRepository.existsByDeckIdAndCardId(any(), anyLong())).willReturn(false);

        given(deckCardRepository.countByDeckId(any())).willReturn(50L);

        assertThatThrownBy(() -> deckCardCommandService.saveToCustomDeck(userId, customDeckId, cardId))
            .isInstanceOf(DeckBusinessException.class)
            .hasMessage(DeckErrorCode.CUSTOM_DECK_CARD_LIMIT_EXCEEDED.message());

        verify(deckCardRepository, never()).save(any());
    }

    @Test
    @DisplayName("커스텀 덱 API에 기본 덱(DEFAULT) ID를 요청하면 예외가 발생한다")
    void saveToCustomDeck_throwsException_whenDeckIsDefault() {
        Deck defaultDeck = Deck.createDefault(userId);
        given(deckRepository.findByIdAndMemberUserId(customDeckId, userId))
            .willReturn(Optional.of(defaultDeck));

        assertThatThrownBy(() -> deckCardCommandService.saveToCustomDeck(userId, customDeckId, cardId))
            .isInstanceOf(DeckBusinessException.class)
            .hasMessage(DeckErrorCode.DEFAULT_DECK_CANNOT_BE_MODIFIED.message());
    }
}
