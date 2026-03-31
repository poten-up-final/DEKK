package com.dekk.card.recommend.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.card.recommend.application.RecommendScoringService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RecommendScoringServiceTest {

    private final RecommendScoringService scoringService = new RecommendScoringService();

    @Nested
    @DisplayName("calculateCategoryPreferenceRatios")
    class CalculateCategoryPreferenceRatios {

        @Test
        @DisplayName("LIKE 이력이 없으면 빈 맵을 반환한다")
        void shouldReturnEmptyMap_whenNoLikedCategories() {
            Map<Long, Double> result = scoringService.calculateCategoryPreferenceRatios(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("카테고리가 하나뿐이면 해당 카테고리의 비율이 1.0이다")
        void shouldReturn1_whenSingleCategory() {
            Map<Long, Double> result = scoringService.calculateCategoryPreferenceRatios(List.of(1L, 1L, 1L));

            assertThat(result).hasSize(1);
            assertThat(result.get(1L)).isCloseTo(1.0, within(0.001));
        }

        @Test
        @DisplayName("모든 카테고리 비율의 합은 1.0이다")
        void shouldSumToOne_whenMultipleCategories() {
            // 카테고리 1: 3회, 카테고리 2: 1회 → 비율 합계 = 1.0
            List<Long> likedCategoryIds = List.of(1L, 1L, 1L, 2L);

            Map<Long, Double> result = scoringService.calculateCategoryPreferenceRatios(likedCategoryIds);

            double sum = result.values().stream().mapToDouble(Double::doubleValue).sum();
            assertThat(sum).isCloseTo(1.0, within(0.001));
        }

        @Test
        @DisplayName("등장 횟수에 비례하여 각 카테고리의 비율이 계산된다")
        void shouldCalculateRatioProportionally() {
            // 카테고리 1: 3회 / 4 = 0.75, 카테고리 2: 1회 / 4 = 0.25
            List<Long> likedCategoryIds = List.of(1L, 1L, 1L, 2L);

            Map<Long, Double> result = scoringService.calculateCategoryPreferenceRatios(likedCategoryIds);

            assertThat(result.get(1L)).isCloseTo(0.75, within(0.001));
            assertThat(result.get(2L)).isCloseTo(0.25, within(0.001));
        }

        @Test
        @DisplayName("모든 카테고리가 동일하게 등장하면 균등한 비율을 반환한다")
        void shouldReturnEqualRatios_whenAllCategoriesAppearOnce() {
            // 카테고리 1, 2, 3 각 1회 → 각 1/3 = 0.333..
            List<Long> likedCategoryIds = List.of(1L, 2L, 3L);

            Map<Long, Double> result = scoringService.calculateCategoryPreferenceRatios(likedCategoryIds);

            assertThat(result.get(1L)).isCloseTo(1.0 / 3, within(0.001));
            assertThat(result.get(2L)).isCloseTo(1.0 / 3, within(0.001));
            assertThat(result.get(3L)).isCloseTo(1.0 / 3, within(0.001));
        }
    }

    @Nested
    @DisplayName("rank")
    class Rank {

        private MemberCardResult card(long cardId, Integer height, Integer weight) {
            return new MemberCardResult(cardId, null, height, weight, List.of(), List.of());
        }

        @Test
        @DisplayName("후보 카드가 없으면 빈 리스트를 반환한다")
        void shouldReturnEmpty_whenNoCandidates() {
            List<MemberCardResult> result = scoringService.rank(
                    175, 70, List.of(), Map.of(), Map.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("카테고리 선호도가 높은 카드가 앞에 정렬된다")
        void shouldRankByCategoryScore_whenBodySizeIsSame() {
            MemberCardResult cardA = card(1L, 175, 70);
            MemberCardResult cardB = card(2L, 175, 70);

            Map<Long, List<Long>> categoryMap = Map.of(
                    1L, List.of(1L),
                    2L, List.of(2L));
            Map<Long, Double> preferences = Map.of(1L, 0.8, 2L, 0.2);

            List<MemberCardResult> result = scoringService.rank(175, 70, List.of(cardA, cardB), categoryMap, preferences);

            assertThat(result.get(0).cardId()).isEqualTo(1L);
            assertThat(result.get(1).cardId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("체형 점수가 높은 카드가 앞에 정렬된다 (카테고리 선호 이력 없을 때)")
        void shouldRankByBodyScore_whenNoCategoryPreferences() {
            // 선호 이력 없음 → categoryScore = 0.0 동일
            // 카드 A: 체형 완전 일치 (bodyScore = 1.0) → totalScore = 0.4
            // 카드 B: 체형 차이 큼   (bodyScore = 0.0) → totalScore = 0.0
            MemberCardResult cardA = card(1L, 175, 70);
            MemberCardResult cardB = card(2L, 190, 90);

            List<MemberCardResult> result = scoringService.rank(
                    175, 70, List.of(cardB, cardA), Map.of(), Map.of());

            assertThat(result.getFirst().cardId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("유저 체형 정보가 없으면 모든 카드의 bodyScore를 1.0으로 계산한다")
        void shouldTreatBodyScoreAs1_whenUserBodyInfoIsNull() {
            // userHeight/Weight = null → bodyScore = 1.0
            // 카드 A: 카테고리 1(선호도 0.8) → totalScore = 0.8 * 0.6 + 1.0 * 0.4 = 0.88
            // 카드 B: 카테고리 2(선호도 0.2) → totalScore = 0.2 * 0.6 + 1.0 * 0.4 = 0.52
            MemberCardResult cardA = card(1L, null, null);
            MemberCardResult cardB = card(2L, null, null);

            Map<Long, List<Long>> categoryMap = Map.of(
                    1L, List.of(1L),
                    2L, List.of(2L));
            Map<Long, Double> preferences = Map.of(1L, 0.8, 2L, 0.2);

            List<MemberCardResult> result = scoringService.rank(null, null, List.of(cardB, cardA), categoryMap, preferences);

            assertThat(result.getFirst().cardId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("totalScore가 같으면 원래 순서를 유지한다")
        void shouldMaintainOrder_whenScoresAreEqual() {
            // 두 카드 모두 카테고리·체형 동일 → totalScore 동일
            MemberCardResult cardA = card(1L, 175, 70);
            MemberCardResult cardB = card(2L, 175, 70);

            Map<Long, List<Long>> categoryMap = Map.of(
                    1L, List.of(1L),
                    2L, List.of(1L));
            Map<Long, Double> preferences = Map.of(1L, 0.5);

            List<MemberCardResult> result = scoringService.rank(175, 70, List.of(cardA, cardB), categoryMap, preferences);

            assertThat(result).hasSize(2);
        }
    }
}
