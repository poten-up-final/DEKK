package com.dekk.app.card.infrastructure;

import com.dekk.app.card.application.dto.query.AdminCardSearchQuery;
import com.dekk.app.card.application.dto.query.RecommendCandidateQuery;
import com.dekk.app.card.domain.model.Card;
import com.dekk.app.card.domain.model.enums.CardStatus;
import com.dekk.app.card.domain.model.enums.Platform;
import com.dekk.app.card.domain.repository.CardRepository;
import com.dekk.app.card.infrastructure.jpa.CardJpaRepository;
import com.dekk.app.card.infrastructure.jpa.CardSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepository {
    private static final int MAX_FETCH_SIZE = 100;
    private static final int MAX_EXCLUDE_SIZE = 1000;
    private static final long NON_EXISTENT_CARD_ID = 0L;

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
        Page<Long> idPage = cardJpaRepository.findCardIdsByStatus(status, pageable);

        List<Long> cardIds = idPage.getContent();
        if (cardIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, idPage.getTotalElements());
        }

        List<Card> cardsWithProducts = cardJpaRepository.findAllByIdInWithProducts(cardIds);

        return new PageImpl<>(cardsWithProducts, pageable, idPage.getTotalElements());
    }

    @Override
    public List<Card> findAllByIdInWithProducts(List<Long> ids) {
        return cardJpaRepository.findAllByIdInWithProducts(ids);
    }

    @Override
    public List<Card> findRecommendCandidates(RecommendCandidateQuery query) {
        return cardJpaRepository.findRecommendCandidates(
                query.genders(), query.minHeight(), query.maxHeight(), query.minWeight(), query.maxWeight());
    }

    @Override
    public Optional<Card> findById(Long id) {
        return cardJpaRepository.findById(id);
    }

    @Override
    public Optional<Card> findByIdWithDetails(Long id) {
        return cardJpaRepository.findByIdWithDetails(id);
    }

    @Override
    public boolean existsById(Long id) {
        return cardJpaRepository.existsById(id);
    }

    @Override
    public Page<Card> searchCards(AdminCardSearchQuery condition, Pageable pageable) {
        return cardJpaRepository.findAll(CardSpecification.searchByCondition(condition), pageable);
    }

    @Override
    public List<Card> findLatestApprovedCardsExcluding(Set<Long> excludeCardIds, int size) {
        int fetchSize = Math.min(size, MAX_FETCH_SIZE);
        List<Long> safeExcludeIds = excludeCardIds.isEmpty()
                ? List.of(NON_EXISTENT_CARD_ID)
                : new ArrayList<>(excludeCardIds).subList(0, Math.min(excludeCardIds.size(), MAX_EXCLUDE_SIZE));

        Pageable pageable = PageRequest.of(0, fetchSize);

        List<Long> cardIds = cardJpaRepository.findLatestApprovedCardIdsExcluding(safeExcludeIds, pageable);

        if (cardIds.isEmpty()) {
            return List.of();
        }

        return cardJpaRepository.findAllByIdInWithProductsOrderByUpdatedAt(cardIds);
    }

    @Override
    public Optional<Card> findByPublicId(UUID publicId) {
        return cardJpaRepository.findByPublicIdWithProducts(publicId);
    }
}
