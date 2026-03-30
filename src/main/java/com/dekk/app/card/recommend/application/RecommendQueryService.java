package com.dekk.app.card.recommend.application;

import com.dekk.app.activelog.application.ActiveLogQueryService;
import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.card.application.CardCategoryQueryService;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.card.application.dto.query.RecommendCandidateQuery;
import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.card.recommend.application.dto.RecommendCardResult;
import com.dekk.app.user.application.UserQueryService;
import com.dekk.app.user.application.dto.result.UserInfoResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendQueryService {

    private static final double RECOMMEND_RATIO = 0.7;

    private final CardQueryService cardQueryService;
    private final UserQueryService userQueryService;
    private final ActiveLogQueryService activeLogQueryService;
    private final CardCategoryQueryService cardCategoryQueryService;
    private final RecommendScoringService recommendScoringService;

    public Slice<RecommendCardResult> getRecommendCards(Long userId, Pageable pageable) {
        int totalNeeded = (int) (pageable.getOffset() + pageable.getPageSize());
        int recommendCount = (int) Math.ceil(totalNeeded * RECOMMEND_RATIO);

        Set<Long> swipedIds = activeLogQueryService.getAllSwipedCardIds(userId);
        List<MemberCardResult> recommendCards =
                rankCandidates(userId, swipedIds).stream().limit(recommendCount).toList();

        Set<Long> recommendIds = extractCardIds(recommendCards);
        int normalCount = totalNeeded - recommendCards.size();
        List<MemberCardResult> normalCards = fetchNormalCards(swipedIds, recommendIds, normalCount);

        List<RecommendCardResult> allResults = mergeRecommendResults(recommendCards, normalCards);
        return toSlice(allResults, pageable, totalNeeded);
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
        Map<Long, Double> preferences = buildCategoryPreferences(userId);
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
