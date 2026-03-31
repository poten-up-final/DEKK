package com.dekk.card.recommend.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.dekk.app.activelog.application.ActiveLogQueryService;
import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.card.application.CardCategoryQueryService;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.card.domain.model.Card;
import com.dekk.app.card.domain.model.CardImage;
import com.dekk.app.card.recommend.application.RecommendQueryService;
import com.dekk.app.card.recommend.application.RecommendScoringService;
import com.dekk.app.card.recommend.application.dto.RecommendCardResult;
import com.dekk.app.user.application.UserQueryService;
import com.dekk.app.user.application.dto.result.UserInfoResult;
import com.dekk.app.user.domain.model.enums.Gender;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RecommendQueryServiceTest {

    private static final Long USER_ID = 1L;
    private static final int SIZE = 10;

    @Mock private CardQueryService cardQueryService;
    @Mock private UserQueryService userQueryService;
    @Mock private ActiveLogQueryService activeLogQueryService;
    @Mock private CardCategoryQueryService cardCategoryQueryService;
    @Mock private RecommendScoringService recommendScoringService;

    @InjectMocks
    private RecommendQueryService recommendQueryService;

    @Nested
    @DisplayName("스와이프 이력 제외")
    class ExcludeSwipedCards {

        @BeforeEach
        void setUp() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(Gender.MALE, 175, 70));
            given(activeLogQueryService.getSwipedCardIds(USER_ID, SwipeType.LIKE)).willReturn(List.of());
            given(cardCategoryQueryService.getCardCategoryMap(any())).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(any())).willReturn(Map.of());
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
            given(cardQueryService.getLatestCards(any(), anyInt())).willReturn(List.of());
        }

        @Test
        @DisplayName("스와이프한 카드는 추천 결과에서 제외된다")
        void shouldExcludeSwipedCards_whenSwipedIdsExist() {
            Card card10 = mockCard(10L);
            Card card20 = mockCard(20L);
            Card card30 = mockCard(30L);

            given(cardQueryService.getRecommendCandidates(any()))
                    .willReturn(List.of(card10, card20, card30));
            given(activeLogQueryService.getAllSwipedCardIds(USER_ID))
                    .willReturn(Set.of(10L, 20L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, SIZE)).getContent();

            List<RecommendCardResult> recommended = recommendedOnly(result);
            assertThat(recommended).hasSize(1);
            assertThat(recommended.getFirst().card().cardId()).isEqualTo(30L);
        }

        @Test
        @DisplayName("스와이프 이력이 없으면 후보 카드 전체가 추천 대상이 된다")
        void shouldIncludeAllCandidates_whenNoSwipeHistory() {
            Card card1 = mockCard(1L);
            Card card2 = mockCard(2L);

            given(cardQueryService.getRecommendCandidates(any()))
                    .willReturn(List.of(card1, card2));
            given(activeLogQueryService.getAllSwipedCardIds(USER_ID))
                    .willReturn(Set.of());

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, SIZE)).getContent();

            assertThat(recommendedOnly(result)).hasSize(2);
        }

        @Test
        @DisplayName("모든 후보 카드를 스와이프했으면 추천 카드는 없다")
        void shouldHaveNoRecommended_whenAllCandidatesSwiped() {
            Card card1 = mockCard(1L);
            Card card2 = mockCard(2L);

            given(cardQueryService.getRecommendCandidates(any()))
                    .willReturn(List.of(card1, card2));
            given(activeLogQueryService.getAllSwipedCardIds(USER_ID))
                    .willReturn(Set.of(1L, 2L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, SIZE)).getContent();

            assertThat(recommendedOnly(result)).isEmpty();
        }
    }

    @Nested
    @DisplayName("추천/일반 카드 혼합 비율")
    class RecommendNormalMix {

        @BeforeEach
        void setUp() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(Gender.MALE, 175, 70));
            given(activeLogQueryService.getAllSwipedCardIds(USER_ID)).willReturn(Set.of());
            given(activeLogQueryService.getSwipedCardIds(USER_ID, SwipeType.LIKE)).willReturn(List.of());
            given(cardCategoryQueryService.getCardCategoryMap(any())).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(any())).willReturn(Map.of());
        }

        @Test
        @DisplayName("size=10이면 추천 7개(70%), 일반 3개(30%)로 구성된다")
        void shouldReturn7Recommended3Normal_whenSize10() {
            List<Card> candidates = mockCards(10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(candidates);
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
            given(cardQueryService.getLatestCards(any(), anyInt()))
                    .willReturn(memberCards(100L, 101L, 102L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, 10)).getContent();

            assertThat(recommendedOnly(result)).hasSize(7);
            assertThat(normalOnly(result)).hasSize(3);
        }

        @Test
        @DisplayName("추천 후보가 부족하면 일반 카드로 나머지를 채운다")
        void shouldFillWithNormalCards_whenRecommendInsufficient() {
            // 추천 후보 3개뿐 → size=10 기준 추천 7개가 필요하지만 3개만 가능
            // → 추천 3개 + 일반 7개로 보충
            List<Card> candidates = mockCards(1L, 2L, 3L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(candidates);
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
            given(cardQueryService.getLatestCards(any(), anyInt()))
                    .willReturn(memberCards(100L, 101L, 102L, 103L, 104L, 105L, 106L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, 10)).getContent();

            assertThat(recommendedOnly(result)).hasSize(3);
            assertThat(normalOnly(result)).hasSize(7);
        }

        @Test
        @DisplayName("recommended=true 카드와 recommended=false 카드가 올바르게 구분된다")
        void shouldTagRecommendedFlagCorrectly() {
            List<Card> candidates = mockCards(1L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(candidates);
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
            given(cardQueryService.getLatestCards(any(), anyInt()))
                    .willReturn(memberCards(100L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, 10)).getContent();

            assertThat(result).anyMatch(RecommendCardResult::recommended);
            assertThat(result).anyMatch(r -> !r.recommended());
        }

        @Test
        @DisplayName("일반 카드에는 추천 카드 ID가 포함되지 않는다")
        void shouldExcludeRecommendedIdsFromNormalCards() {
            List<Card> candidates = mockCards(1L, 2L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(candidates);
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
            // 일반 카드 ID 100, 101은 추천 카드 ID와 겹치지 않음
            given(cardQueryService.getLatestCards(any(), anyInt()))
                    .willReturn(memberCards(100L, 101L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, 10)).getContent();

            Set<Long> recommendedIds = recommendedOnly(result).stream()
                    .map(r -> r.card().cardId())
                    .collect(java.util.stream.Collectors.toSet());
            Set<Long> normalIds = normalOnly(result).stream()
                    .map(r -> r.card().cardId())
                    .collect(java.util.stream.Collectors.toSet());

            assertThat(recommendedIds).doesNotContainAnyElementsOf(normalIds);
        }
    }

    @Nested
    @DisplayName("유저 프로파일 기반 필터링")
    class ProfileBasedFiltering {

        @BeforeEach
        void setUp() {
            given(cardQueryService.getRecommendCandidates(any())).willReturn(List.of());
            given(activeLogQueryService.getAllSwipedCardIds(USER_ID)).willReturn(Set.of());
            given(activeLogQueryService.getSwipedCardIds(USER_ID, SwipeType.LIKE)).willReturn(List.of());
            given(cardCategoryQueryService.getCardCategoryMap(any())).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(any())).willReturn(Map.of());
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
            given(cardQueryService.getLatestCards(any(), anyInt())).willReturn(List.of());
        }

        @Test
        @DisplayName("성별 정보가 없으면 전체 성별 대상으로 카드가 조회된다")
        void shouldQueryAllGenders_whenGenderIsNull() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(null, 170, 65));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, SIZE)).getContent();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("체형 정보가 없으면 기본 범위(전체)로 카드가 조회된다")
        void shouldUseDefaultRange_whenBodyInfoIsNull() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(Gender.MALE, null, null));
            List<Card> candidates = mockCards(1L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(candidates);

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, SIZE)).getContent();

            assertThat(recommendedOnly(result)).hasSize(1);
        }
    }

    @Nested
    @DisplayName("LIKE 카드 기반 카테고리 선호도 반영")
    class CategoryPreference {

        @BeforeEach
        void setUp() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(Gender.MALE, 175, 70));
            given(cardQueryService.getRecommendCandidates(any())).willReturn(List.of());
            given(activeLogQueryService.getAllSwipedCardIds(USER_ID)).willReturn(Set.of());
            given(cardQueryService.getLatestCards(any(), anyInt())).willReturn(List.of());
        }

        @Test
        @DisplayName("LIKE 이력이 없으면 빈 선호 맵으로 랭킹을 수행한다")
        void shouldRankWithEmptyPreferences_whenNoLikeHistory() {
            given(activeLogQueryService.getSwipedCardIds(USER_ID, SwipeType.LIKE)).willReturn(List.of());
            given(cardCategoryQueryService.getCardCategoryMap(List.of())).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(List.of())).willReturn(Map.of());
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, SIZE)).getContent();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("LIKE한 카드에 카테고리 매핑이 없으면 빈 선호 맵으로 랭킹을 수행한다")
        void shouldRankWithEmptyPreferences_whenLikedCardsHaveNoCategories() {
            given(activeLogQueryService.getSwipedCardIds(USER_ID, SwipeType.LIKE)).willReturn(List.of(10L, 20L));
            given(cardCategoryQueryService.getCardCategoryMap(List.of(10L, 20L))).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(List.of())).willReturn(Map.of());
            given(cardCategoryQueryService.getCardCategoryMap(List.of())).willReturn(Map.of());
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, SIZE)).getContent();

            assertThat(result).isEmpty();
        }
    }

    private List<RecommendCardResult> recommendedOnly(List<RecommendCardResult> result) {
        return result.stream().filter(RecommendCardResult::recommended).toList();
    }

    private List<RecommendCardResult> normalOnly(List<RecommendCardResult> result) {
        return result.stream().filter(r -> !r.recommended()).toList();
    }

    private UserInfoResult userInfo(Gender gender, Integer height, Integer weight) {
        return new UserInfoResult(USER_ID, "test@test.com", "닉네임", height, weight, gender, "ACTIVE", "USER");
    }

    private Card mockCard(Long cardId) {
        Card card = mock(Card.class);
        CardImage cardImage = mock(CardImage.class);
        given(card.getId()).willReturn(cardId);
        given(card.getCardImage()).willReturn(cardImage);
        given(cardImage.getImageUrl()).willReturn("http://image.url/" + cardId);
        given(card.getHeight()).willReturn(170);
        given(card.getWeight()).willReturn(65);
        given(card.getTags()).willReturn(null);
        given(card.getCardProducts()).willReturn(List.of());
        return card;
    }

    private List<Card> mockCards(Long... ids) {
        return java.util.Arrays.stream(ids).map(this::mockCard).toList();
    }

    private List<MemberCardResult> memberCards(Long... ids) {
        return java.util.Arrays.stream(ids)
                .map(id -> new MemberCardResult(id, null, null, null, List.of(), List.of()))
                .toList();
    }
}
