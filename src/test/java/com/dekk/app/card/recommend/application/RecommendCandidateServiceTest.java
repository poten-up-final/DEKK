package com.dekk.app.card.recommend.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.dekk.app.activelog.domain.model.SwipedCards;
import com.dekk.app.card.application.CardCategoryQueryService;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.card.application.dto.query.RecommendCandidateQuery;
import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.card.domain.model.Card;
import com.dekk.app.card.domain.model.CardImage;
import com.dekk.app.card.domain.model.enums.TargetGender;
import com.dekk.app.user.application.UserQueryService;
import com.dekk.app.user.application.dto.result.UserInfoResult;
import com.dekk.app.user.domain.model.enums.Gender;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendCandidateServiceTest {

    private static final Long USER_ID = 1L;

    @Mock private CardQueryService cardQueryService;
    @Mock private UserQueryService userQueryService;
    @Mock private CardCategoryQueryService cardCategoryQueryService;
    @Mock private RecommendScoringService recommendScoringService;

    private RecommendCandidateService candidateService;

    @BeforeEach
    void setUp() {
        candidateService = new RecommendCandidateService(
                cardQueryService, userQueryService, cardCategoryQueryService,
                recommendScoringService, new RecommendProperties(0.7, 10, 500));
    }

    @Nested
    @DisplayName("스와이프 이력 제외")
    class ExcludeSwipedCards {

        @BeforeEach
        void setUp() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(Gender.MALE, 175, 70));
            given(cardCategoryQueryService.getCardCategoryMap(any())).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(any())).willReturn(Map.of());
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
        }

        @Test
        @DisplayName("스와이프한 카드는 후보군에서 제외된다")
        void shouldExcludeSwipedCards_whenSwipedIdsExist() {
            Card c10 = mockCard(10L), c20 = mockCard(20L), c30 = mockCard(30L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(List.of(c10, c20, c30));

            List<MemberCardResult> result = candidateService.rankCandidates(
                    USER_ID, new SwipedCards(Set.of(10L, 20L), Set.of()));

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().cardId()).isEqualTo(30L);
        }

        @Test
        @DisplayName("스와이프 이력이 없으면 후보 카드 전체가 반환된다")
        void shouldIncludeAllCandidates_whenNoSwipeHistory() {
            Card c1 = mockCard(1L), c2 = mockCard(2L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(List.of(c1, c2));

            List<MemberCardResult> result = candidateService.rankCandidates(USER_ID, SwipedCards.empty());

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("모든 후보 카드를 스와이프했으면 빈 목록을 반환한다")
        void shouldReturnEmpty_whenAllCandidatesSwiped() {
            Card c1 = mockCard(1L), c2 = mockCard(2L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(List.of(c1, c2));

            List<MemberCardResult> result = candidateService.rankCandidates(
                    USER_ID, new SwipedCards(Set.of(1L, 2L), Set.of()));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("유저 프로파일 기반 필터링")
    class ProfileBasedFiltering {

        @BeforeEach
        void setUp() {
            given(cardQueryService.getRecommendCandidates(any())).willReturn(List.of());
            given(cardCategoryQueryService.getCardCategoryMap(any())).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(any())).willReturn(Map.of());
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
        }

        @Test
        @DisplayName("성별 정보가 없으면 모든 TargetGender를 포함한 쿼리로 조회된다")
        void shouldQueryAllGenders_whenGenderIsNull() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(null, 170, 65));
            ArgumentCaptor<RecommendCandidateQuery> captor =
                    ArgumentCaptor.forClass(RecommendCandidateQuery.class);

            candidateService.rankCandidates(USER_ID, SwipedCards.empty());

            then(cardQueryService).should().getRecommendCandidates(captor.capture());
            assertThat(captor.getValue().genders())
                    .containsExactlyInAnyOrderElementsOf(Arrays.asList(TargetGender.values()));
        }

        @Test
        @DisplayName("MALE이면 MEN/OTHER만 포함한 쿼리로 조회된다")
        void shouldQueryMaleGenders_whenGenderIsMale() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(Gender.MALE, 175, 70));
            ArgumentCaptor<RecommendCandidateQuery> captor =
                    ArgumentCaptor.forClass(RecommendCandidateQuery.class);

            candidateService.rankCandidates(USER_ID, SwipedCards.empty());

            then(cardQueryService).should().getRecommendCandidates(captor.capture());
            assertThat(captor.getValue().genders())
                    .containsExactlyInAnyOrder(TargetGender.MEN, TargetGender.OTHER);
        }

        @Test
        @DisplayName("체형 정보가 없으면 기본 범위(0~999)로 카드가 조회된다")
        void shouldUseDefaultRange_whenBodyInfoIsNull() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(Gender.MALE, null, null));
            ArgumentCaptor<RecommendCandidateQuery> captor =
                    ArgumentCaptor.forClass(RecommendCandidateQuery.class);
            Card c1 = mockCard(1L);
            given(cardQueryService.getRecommendCandidates(any())).willReturn(List.of(c1));

            candidateService.rankCandidates(USER_ID, SwipedCards.empty());

            then(cardQueryService).should().getRecommendCandidates(captor.capture());
            RecommendCandidateQuery query = captor.getValue();
            assertThat(query.minHeight()).isZero();
            assertThat(query.maxHeight()).isEqualTo(999);
            assertThat(query.minWeight()).isZero();
            assertThat(query.maxWeight()).isEqualTo(999);
        }
    }

    @Nested
    @DisplayName("LIKE 카드 기반 카테고리 선호도 반영")
    class CategoryPreference {

        @BeforeEach
        void setUp() {
            given(userQueryService.getMyInfo(USER_ID)).willReturn(userInfo(Gender.MALE, 175, 70));
            given(cardQueryService.getRecommendCandidates(any())).willReturn(List.of());
            given(recommendScoringService.rank(any(), any(), any(), any(), any()))
                    .willAnswer(inv -> inv.getArgument(2));
        }

        @Test
        @DisplayName("LIKE 이력이 없으면 빈 선호 맵으로 랭킹을 수행한다")
        void shouldRankWithEmptyPreferences_whenNoLikeHistory() {
            given(cardCategoryQueryService.getCardCategoryMap(List.of())).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(List.of())).willReturn(Map.of());

            List<MemberCardResult> result = candidateService.rankCandidates(USER_ID, SwipedCards.empty());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("LIKE한 카드에 카테고리 매핑이 없으면 빈 선호 맵으로 랭킹을 수행한다")
        void shouldRankWithEmptyPreferences_whenLikedCardsHaveNoCategories() {
            given(cardCategoryQueryService.getCardCategoryMap(any())).willReturn(Map.of());
            given(recommendScoringService.calculateCategoryPreferenceRatios(any())).willReturn(Map.of());

            List<MemberCardResult> result = candidateService.rankCandidates(
                    USER_ID, new SwipedCards(Set.of(10L, 20L), Set.of()));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("일반 카드 fetch")
    class FetchNormalCards {

        @Test
        @DisplayName("추천 카드 ID는 일반 카드 결과에서 제외된다")
        void shouldExcludeRecommendIds_fromNormalCards() {
            given(cardQueryService.getLatestCards(any(), anyInt()))
                    .willReturn(List.of(
                            memberCard(1L), memberCard(2L), memberCard(100L), memberCard(101L)));

            List<MemberCardResult> result = candidateService.fetchNormalCards(
                    Set.of(), Set.of(1L, 2L), 3);

            assertThat(result).hasSize(2);
            assertThat(result.stream().map(MemberCardResult::cardId).toList())
                    .containsExactly(100L, 101L);
        }
    }

    private UserInfoResult userInfo(Gender gender, Integer height, Integer weight) {
        return new UserInfoResult(USER_ID, "test@test.com", "닉네임", height, weight, gender, "ACTIVE", "USER");
    }

    private Card mockCard(Long cardId) {
        Card card = mock(Card.class);
        CardImage cardImage = mock(CardImage.class);
        given(card.getId()).willReturn(cardId);
        given(card.getPublicId()).willReturn(UUID.randomUUID());
        given(card.getCardImage()).willReturn(cardImage);
        given(cardImage.getImageUrl()).willReturn("http://image.url/" + cardId);
        given(card.getHeight()).willReturn(170);
        given(card.getWeight()).willReturn(65);
        given(card.getTags()).willReturn(null);
        given(card.getCardProducts()).willReturn(List.of());
        return card;
    }

    private MemberCardResult memberCard(Long id) {
        return new MemberCardResult(id, null, null, null, null, List.of(), List.of());
    }
}
