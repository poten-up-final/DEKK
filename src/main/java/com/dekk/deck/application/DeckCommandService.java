package com.dekk.deck.application;

import com.dekk.deck.domain.model.Deck;
import com.dekk.deck.domain.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeckCommandService {

    private final DeckRepository deckRepository;

    @Transactional
    public void createDefaultDeck(Long userId) {
        if (deckRepository.findByUserIdAndIsDefaultTrue(userId).isPresent()) {
            return;
        }

        Deck defaultDeck = Deck.create(userId, "나의 기본 보관함", true);
        deckRepository.save(defaultDeck);
    }
}
