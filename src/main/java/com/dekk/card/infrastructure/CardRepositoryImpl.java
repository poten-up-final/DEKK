package com.dekk.card.infrastructure;

import com.dekk.card.domain.model.Card;
import com.dekk.card.domain.model.enums.CardStatus;
import com.dekk.card.domain.model.enums.Platform;
import com.dekk.card.domain.repository.CardRepository;
import com.dekk.card.infrastructure.jpa.CardJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepository {
    private final CardJpaRepository cardJpaRepository;

    @Override
    public Card save(Card card) {
        return cardJpaRepository.save(card);
    }

    @Override
    public List<Card> saveAll(List<Card> cards) {
        return cardJpaRepository.saveAll(cards);
    }

    @Override
    public boolean existsByPlatformAndOriginId(Platform platform, String originId) {
        return cardJpaRepository.existsByPlatformAndOriginId(platform, originId);
    }

    @Override
    public Page<Card> findCardsWithImageByStatus(CardStatus status, Pageable pageable) {
        return cardJpaRepository.findCardsWithImageByStatus(status, pageable);
    }

    @Override
    public Page<Card> findCardsWithProductsByStatus(CardStatus status, Pageable pageable) {
        return cardJpaRepository.findCardsWithProductsByStatus(status, pageable);
    }

    @Override
    public List<Card> findAllByIdInWithProducts(List<Long> ids) {
        return cardJpaRepository.findAllByIdInWithProducts(ids);
    }
}
