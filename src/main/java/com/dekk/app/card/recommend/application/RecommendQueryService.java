package com.dekk.app.card.recommend.application;

import com.dekk.app.activelog.application.ActiveLogQueryService;
import com.dekk.app.activelog.domain.model.SwipedCards;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.card.application.dto.result.GuestCardResult;
import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.card.recommend.application.dto.RecommendCardResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendQueryService {

    private final CardQueryService cardQueryService;
    private final ActiveLogQueryService activeLogQueryService;
    private final RecommendCandidateService candidateService;
    private final RecommendProperties properties;

    public Slice<RecommendCardResult> getRecommendCards(Long userId, Pageable pageable) {
        return getRecommendCards(userId, pageable, null);
    }

    public Slice<GuestCardResult> getGuestCards(Pageable pageable, UUID startCardId) {
        Slice<GuestCardResult> randomCards = cardQueryService.getCardsForGuestRandom(pageable);

        if (startCardId == null) {
            return randomCards;
        }

        Optional<GuestCardResult> startCard = cardQueryService.findByPublicIdForGuest(startCardId);
        if (startCard.isEmpty()) {
            return randomCards;
        }

        GuestCardResult card = startCard.get();
        List<GuestCardResult> merged = new ArrayList<>(randomCards.getContent().size() + 1);
        merged.add(card);
        randomCards.getContent().stream()
                .filter(r -> !r.publicId().equals(startCardId))
                .forEach(merged::add);

        List<GuestCardResult> content =
                merged.stream().limit(pageable.getPageSize()).toList();
        return new SliceImpl<>(content, pageable, randomCards.hasNext());
    }

    public Slice<RecommendCardResult> getRecommendCards(Long userId, Pageable pageable, UUID startCardId) {
        int totalNeeded = (int) (pageable.getOffset() + pageable.getPageSize());
        int recommendCount = (int) Math.ceil(totalNeeded * properties.ratio());

        if (pageable.getPageNumber() >= properties.deepScrollPageThreshold()) {
            log.warn("[Recommend] 깊은 스크롤 감지 userId={} page={} - 컨텐츠 다양성 부족 가능성", userId, pageable.getPageNumber());
        }

        SwipedCards swiped = activeLogQueryService.getSwipedCards(userId);
        Set<Long> swipedIds = swiped.allSwipedIds();
        log.debug(
                "[Recommend] userId={} swipedCount={} totalNeeded={} recommendTarget={}",
                userId,
                swipedIds.size(),
                totalNeeded,
                recommendCount);

        List<MemberCardResult> rankedCandidates = candidateService.rankCandidates(userId, swiped);

        if (rankedCandidates.size() < recommendCount) {
            log.warn(
                    "[Recommend] 후보군 부족 userId={} candidateCount={} recommendTarget={}",
                    userId,
                    rankedCandidates.size(),
                    recommendCount);
        }

        List<MemberCardResult> recommendCards =
                rankedCandidates.stream().limit(recommendCount).toList();
        Set<Long> recommendIds =
                recommendCards.stream().map(MemberCardResult::cardId).collect(Collectors.toSet());
        int normalCount = totalNeeded - recommendCards.size();
        List<MemberCardResult> normalCards = candidateService.fetchNormalCards(swipedIds, recommendIds, normalCount);

        log.debug(
                "[Recommend] userId={} served: recommend={} normal={}",
                userId,
                recommendCards.size(),
                normalCards.size());

        List<RecommendCardResult> allResults = mergeResults(recommendCards, normalCards);

        if (startCardId != null && pageable.getPageNumber() == 0) {
            allResults = prependStartCard(startCardId, allResults);
        }

        return toSlice(allResults, pageable, totalNeeded);
    }

    private List<RecommendCardResult> prependStartCard(UUID startCardId, List<RecommendCardResult> results) {
        Optional<MemberCardResult> startCard = cardQueryService.findByPublicId(startCardId);
        if (startCard.isEmpty()) {
            return results;
        }
        MemberCardResult card = startCard.get();
        List<RecommendCardResult> merged = new ArrayList<>(results.size() + 1);
        merged.add(RecommendCardResult.recommended(card));
        results.stream().filter(r -> !r.card().publicId().equals(startCardId)).forEach(merged::add);
        return merged;
    }

    private List<RecommendCardResult> mergeResults(
            List<MemberCardResult> recommendCards, List<MemberCardResult> normalCards) {
        return Stream.concat(
                        recommendCards.stream().map(RecommendCardResult::recommended),
                        normalCards.stream().map(RecommendCardResult::normal))
                .toList();
    }

    private Slice<RecommendCardResult> toSlice(
            List<RecommendCardResult> allResults, Pageable pageable, int totalNeeded) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allResults.size());
        List<RecommendCardResult> content = start >= allResults.size() ? List.of() : allResults.subList(start, end);
        boolean hasNext = allResults.size() >= totalNeeded;
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
