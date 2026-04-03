package com.dekk.app.card.recommend.application;

import com.dekk.app.activelog.application.ActiveLogQueryService;
import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.card.application.CardCategoryQueryService;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.card.application.dto.query.RecommendCandidateQuery;
import com.dekk.app.card.application.dto.result.GuestCardResult;
import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.card.recommend.application.dto.RecommendCardResult;
import com.dekk.app.user.application.UserQueryService;
import com.dekk.app.user.application.dto.result.UserInfoResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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

    private static final double RECOMMEND_RATIO = 0.7;
    private static final int DEEP_SCROLL_PAGE_THRESHOLD = 10;
    private static final int LARGE_CANDIDATE_THRESHOLD = 500;

    private final CardQueryService cardQueryService;
    private final UserQueryService userQueryService;
    private final ActiveLogQueryService activeLogQueryService;
    private final CardCategoryQueryService cardCategoryQueryService;
    private final RecommendScoringService recommendScoringService;

    public Slice<RecommendCardResult> getRecommendCards(Long userId, Pageable pageable) {
        return getRecommendCards(userId, pageable, null);
    }

    public Slice<GuestCardResult> getGuestCards(Pageable pageable) {
        return cardQueryService.getCardsForGuestRandom(pageable);
    }

    public Slice<RecommendCardResult> getRecommendCards(Long userId, Pageable pageable, UUID startCardId) {
        int totalNeeded = (int) (pageable.getOffset() + pageable.getPageSize());
        int recommendCount = (int) Math.ceil(totalNeeded * RECOMMEND_RATIO);

        if (pageable.getPageNumber() >= DEEP_SCROLL_PAGE_THRESHOLD) {
            log.warn("[Recommend] 깊은 스크롤 감지 userId={} page={} - 컨텐츠 다양성 부족 가능성", userId, pageable.getPageNumber());
        }

        Set<Long> swipedIds = activeLogQueryService.getAllSwipedCardIds(userId);
        log.debug(
                "[Recommend] userId={} swipedCount={} totalNeeded={} recommendTarget={}",
                userId,
                swipedIds.size(),
                totalNeeded,
                recommendCount);

        List<MemberCardResult> rankedCandidates = rankCandidates(userId, swipedIds);

        if (rankedCandidates.size() < recommendCount) {
            log.warn(
                    "[Recommend] 후보군 부족 userId={} candidateCount={} recommendTarget={}",
                    userId,
                    rankedCandidates.size(),
                    recommendCount);
        }

        List<MemberCardResult> recommendCards =
                rankedCandidates.stream().limit(recommendCount).toList();
        Set<Long> recommendIds = extractCardIds(recommendCards);
        int normalCount = totalNeeded - recommendCards.size();
        List<MemberCardResult> normalCards = fetchNormalCards(swipedIds, recommendIds, normalCount);

        log.debug(
                "[Recommend] userId={} served: recommend={} normal={}",
                userId,
                recommendCards.size(),
                normalCards.size());

        List<RecommendCardResult> allResults = mergeRecommendResults(recommendCards, normalCards);

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

    private Set<Long> extractCardIds(List<MemberCardResult> cards) {
        return cards.stream().map(MemberCardResult::cardId).collect(Collectors.toSet());
    }

    // 추천 카드 IDs는 DB exclude 대신 in-memory 필터로 확실히 제거
    // recommendIds.size()만큼 오버 패치하여 필터 후에도 normalCount를 채울 수 있도록 보장
    private List<MemberCardResult> fetchNormalCards(Set<Long> swipedIds, Set<Long> recommendIds, int normalCount) {
        List<MemberCardResult> candidates =
                cardQueryService.getLatestCards(swipedIds, normalCount + recommendIds.size());
        return candidates.stream()
                .filter(c -> !recommendIds.contains(c.cardId()))
                .limit(normalCount)
                .toList();
    }

    private List<RecommendCardResult> mergeRecommendResults(
            List<MemberCardResult> recommendCards, List<MemberCardResult> normalCards) {
        List<RecommendCardResult> results = new ArrayList<>(recommendCards.size() + normalCards.size());
        recommendCards.forEach(c -> results.add(RecommendCardResult.recommended(c)));
        normalCards.forEach(c -> results.add(RecommendCardResult.normal(c)));
        return results;
    }

    private Slice<RecommendCardResult> toSlice(
            List<RecommendCardResult> allResults, Pageable pageable, int totalNeeded) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allResults.size());
        List<RecommendCardResult> content = start >= allResults.size() ? List.of() : allResults.subList(start, end);
        boolean hasNext = allResults.size() >= totalNeeded;
        return new SliceImpl<>(content, pageable, hasNext);
    }

    private List<MemberCardResult> rankCandidates(Long userId, Set<Long> swipedIds) {
        UserInfoResult userInfo = userQueryService.getMyInfo(userId);
        List<MemberCardResult> candidates = fetchCandidates(userInfo).stream()
                .filter(card -> !swipedIds.contains(card.cardId()))
                .toList();

        if (candidates.size() >= LARGE_CANDIDATE_THRESHOLD) {
            log.warn("[Recommend] 대용량 후보군 스코어링 userId={} candidateCount={} - 인메모리 부하 위험", userId, candidates.size());
        }
        Map<Long, Double> preferences = buildCategoryPreferences(userId);

        if (preferences.isEmpty()) {
            log.info("[Recommend] cold-start userId={} (카테고리 선호 없음, 체형 기반으로만 추천)", userId);
        }
        log.debug(
                "[Recommend] scoring userId={} candidateCount={} preferenceCategories={}",
                userId,
                candidates.size(),
                preferences.size());

        Map<Long, List<Long>> cardCategoryMap = cardCategoryQueryService.getCardCategoryMap(
                candidates.stream().map(MemberCardResult::cardId).toList());

        return recommendScoringService.rank(
                userInfo.height(), userInfo.weight(), candidates, cardCategoryMap, preferences);
    }

    private Map<Long, Double> buildCategoryPreferences(Long userId) {
        List<Long> likedCategoryIds = getLikedCategoryIds(userId);
        return recommendScoringService.calculateCategoryPreferenceRatios(likedCategoryIds);
    }

    private List<MemberCardResult> fetchCandidates(UserInfoResult userInfo) {
        return cardQueryService
                .getRecommendCandidates(RecommendCandidateQuery.of(
                        TargetGenderResolver.resolve(userInfo.gender()), userInfo.height(), userInfo.weight()))
                .stream()
                .map(MemberCardResult::from)
                .toList();
    }

    private List<Long> getLikedCategoryIds(Long userId) {
        List<Long> likedCardIds = activeLogQueryService.getSwipedCardIds(userId, SwipeType.LIKE);
        return cardCategoryQueryService.getCardCategoryMap(likedCardIds).values().stream()
                .flatMap(List::stream)
                .toList();
    }
}
