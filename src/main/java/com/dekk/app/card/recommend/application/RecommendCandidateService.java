package com.dekk.app.card.recommend.application;

import com.dekk.app.activelog.domain.model.SwipedCards;
import com.dekk.app.card.application.CardCategoryQueryService;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.card.application.dto.query.RecommendCandidateQuery;
import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.user.application.UserQueryService;
import com.dekk.app.user.application.dto.result.UserInfoResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendCandidateService {

    private final CardQueryService cardQueryService;
    private final UserQueryService userQueryService;
    private final CardCategoryQueryService cardCategoryQueryService;
    private final RecommendScoringService recommendScoringService;
    private final RecommendProperties properties;

    public List<MemberCardResult> rankCandidates(Long userId, SwipedCards swiped) {
        UserInfoResult userInfo = userQueryService.getMyInfo(userId);
        List<MemberCardResult> candidates = fetchCandidates(userInfo).stream()
                .filter(card -> !swiped.isAlreadySwiped(card.cardId()))
                .toList();

        if (candidates.size() >= properties.largeCandidateThreshold()) {
            log.warn("[Recommend] 대용량 후보군 스코어링 userId={} candidateCount={} - 인메모리 부하 위험", userId, candidates.size());
        }

        Map<Long, Double> preferences = buildCategoryPreferences(swiped.likedIds());

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

    // recommendIds.size()만큼 오버 패치하여 필터 후에도 normalCount를 채울 수 있도록 보장
    public List<MemberCardResult> fetchNormalCards(Set<Long> swipedIds, Set<Long> recommendIds, int normalCount) {
        List<MemberCardResult> candidates =
                cardQueryService.getLatestCards(swipedIds, normalCount + recommendIds.size());
        return candidates.stream()
                .filter(c -> !recommendIds.contains(c.cardId()))
                .limit(normalCount)
                .toList();
    }

    private List<MemberCardResult> fetchCandidates(UserInfoResult userInfo) {
        return cardQueryService
                .getRecommendCandidates(RecommendCandidateQuery.of(
                        TargetGenderResolver.resolve(userInfo.gender()), userInfo.height(), userInfo.weight()))
                .stream()
                .map(MemberCardResult::from)
                .toList();
    }

    private Map<Long, Double> buildCategoryPreferences(Set<Long> likedIds) {
        return recommendScoringService.calculateCategoryPreferenceRatios(getLikedCategoryIds(likedIds));
    }

    private List<Long> getLikedCategoryIds(Set<Long> likedIds) {
        if (likedIds.isEmpty()) {
            return List.of();
        }
        return cardCategoryQueryService.getCardCategoryMap(new ArrayList<>(likedIds)).values().stream()
                .flatMap(List::stream)
                .toList();
    }
}
