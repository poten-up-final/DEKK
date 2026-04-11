package com.dekk.app.card.application;

import com.dekk.app.card.application.dto.query.AdminCardSearchQuery;
import com.dekk.app.card.application.dto.query.RecommendCandidateQuery;
import com.dekk.app.card.application.dto.result.AdminCardDetailResult;
import com.dekk.app.card.application.dto.result.AdminCardResult;
import com.dekk.app.card.application.dto.result.GuestCardResult;
import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.card.domain.exception.CardBusinessException;
import com.dekk.app.card.domain.exception.CardErrorCode;
import com.dekk.app.card.domain.model.Card;
import com.dekk.app.card.domain.model.enums.CardStatus;
import com.dekk.app.card.domain.repository.CardRepository;
import com.dekk.app.category.application.CategoryQueryService;
import com.dekk.app.category.application.dto.CategoryListResult;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardQueryService {
    private final CardRepository cardRepository;
    private final CardCategoryQueryService cardCategoryQueryService;
    private final CategoryQueryService categoryQueryService;

    public Page<GuestCardResult> getCardsForGuest(Pageable pageable) {
        return cardRepository
                .findCardsWithImageByStatus(CardStatus.APPROVED, pageable)
                .map(GuestCardResult::from);
    }

    public Slice<GuestCardResult> getCardsForGuestRandom(Pageable pageable) {
        return cardRepository
                .findCardsWithImageByStatusRandom(CardStatus.APPROVED, pageable)
                .map(GuestCardResult::from);
    }

    public Page<MemberCardResult> getCardsForMember(Pageable pageable) {
        return cardRepository
                .findCardsWithProductsByStatus(CardStatus.APPROVED, pageable)
                .map(MemberCardResult::from);
    }

    public List<MemberCardResult> getCardsByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return cardRepository.findAllByIdInWithProducts(ids).stream()
                .map(MemberCardResult::from)
                .toList();
    }

    public Page<AdminCardResult> searchCardsForAdmin(AdminCardSearchQuery query, Pageable pageable) {
        return cardRepository.searchCards(query, pageable).map(AdminCardResult::from);
    }

    public AdminCardDetailResult getCardDetailForAdmin(Long cardId) {
        Card card = cardRepository
                .findByIdWithDetails(cardId)
                .orElseThrow(() -> new CardBusinessException(CardErrorCode.CARD_NOT_FOUND));

        List<Long> categoryIds = cardCategoryQueryService.getCategoryIdsByCardId(cardId);
        List<CategoryListResult> categoryResults = categoryQueryService.getCategoriesByIds(categoryIds);

        return AdminCardDetailResult.of(card, categoryResults);
    }

    public List<Card> getRecommendCandidates(RecommendCandidateQuery query) {
        return cardRepository.findRecommendCandidates(query);
    }

    public List<MemberCardResult> getLatestCards(Set<Long> excludeCardIds, int size) {
        return cardRepository.findLatestApprovedCardsExcluding(excludeCardIds, size).stream()
                .map(MemberCardResult::from)
                .toList();
    }

    public Optional<MemberCardResult> findByPublicId(UUID publicId) {
        return cardRepository.findByPublicId(publicId).map(MemberCardResult::from);
    }

    public Long getCardIdByPublicId(UUID publicId) {
        return cardRepository
                .findByPublicId(publicId)
                .map(Card::getId)
                .orElseThrow(() -> new CardBusinessException(CardErrorCode.CARD_NOT_FOUND));
    }
}
