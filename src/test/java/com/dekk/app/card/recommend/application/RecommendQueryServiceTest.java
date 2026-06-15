package com.dekk.app.card.recommend.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;

import com.dekk.app.activelog.application.ActiveLogQueryService;
import com.dekk.app.activelog.domain.model.SwipedCards;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.card.application.dto.result.GuestCardResult;
import com.dekk.app.card.application.dto.result.MemberCardResult;
import com.dekk.app.card.recommend.application.dto.RecommendCardResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class RecommendQueryServiceTest {

    private static final Long USER_ID = 1L;
    private static final int SIZE = 10;

    @Mock private CardQueryService cardQueryService;
    @Mock private ActiveLogQueryService activeLogQueryService;
    @Mock private RecommendCandidateService candidateService;

    private RecommendQueryService recommendQueryService;

    @BeforeEach
    void setUp() {
        recommendQueryService = new RecommendQueryService(
                cardQueryService, activeLogQueryService, candidateService,
                new RecommendProperties(0.7, 10, 500));
    }

    @Nested
    @DisplayName("추천/일반 카드 혼합 비율")
    class RecommendNormalMix {

        @BeforeEach
        void setUp() {
            given(activeLogQueryService.getSwipedCards(USER_ID)).willReturn(SwipedCards.empty());
        }

        @Test
        @DisplayName("size=10이면 추천 7개(70%), 일반 3개(30%)로 구성된다")
        void shouldReturn7Recommended3Normal_whenSize10() {
            given(candidateService.rankCandidates(any(), any()))
                    .willReturn(memberCards(10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L));
            given(candidateService.fetchNormalCards(anySet(), anySet(), anyInt()))
                    .willReturn(memberCards(100L, 101L, 102L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, 10)).getContent();

            assertThat(recommendedOnly(result)).hasSize(7);
            assertThat(normalOnly(result)).hasSize(3);
        }

        @Test
        @DisplayName("추천 후보가 부족하면 일반 카드로 나머지를 채운다")
        void shouldFillWithNormalCards_whenRecommendInsufficient() {
            given(candidateService.rankCandidates(any(), any()))
                    .willReturn(memberCards(1L, 2L, 3L));
            given(candidateService.fetchNormalCards(anySet(), anySet(), anyInt()))
                    .willReturn(memberCards(100L, 101L, 102L, 103L, 104L, 105L, 106L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, 10)).getContent();

            assertThat(recommendedOnly(result)).hasSize(3);
            assertThat(normalOnly(result)).hasSize(7);
        }

        @Test
        @DisplayName("recommended=true 카드와 recommended=false 카드가 올바르게 구분된다")
        void shouldTagRecommendedFlagCorrectly() {
            given(candidateService.rankCandidates(any(), any())).willReturn(memberCards(1L));
            given(candidateService.fetchNormalCards(anySet(), anySet(), anyInt()))
                    .willReturn(memberCards(100L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, 10)).getContent();

            assertThat(result).anyMatch(RecommendCardResult::recommended);
            assertThat(result).anyMatch(r -> !r.recommended());
        }

        @Test
        @DisplayName("일반 카드에는 추천 카드 ID가 포함되지 않는다")
        void shouldExcludeRecommendedIdsFromNormalCards() {
            given(candidateService.rankCandidates(any(), any())).willReturn(memberCards(1L, 2L));
            given(candidateService.fetchNormalCards(anySet(), anySet(), anyInt()))
                    .willReturn(memberCards(100L, 101L));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PageRequest.of(0, 10)).getContent();

            java.util.Set<Long> recommendedIds = recommendedOnly(result).stream()
                    .map(r -> r.card().cardId())
                    .collect(java.util.stream.Collectors.toSet());
            java.util.Set<Long> normalIds = normalOnly(result).stream()
                    .map(r -> r.card().cardId())
                    .collect(java.util.stream.Collectors.toSet());

            assertThat(recommendedIds).doesNotContainAnyElementsOf(normalIds);
        }
    }

    @Nested
    @DisplayName("회원 SEO startCardId 처리")
    class MemberStartCard {

        private final UUID START_UUID = UUID.randomUUID();
        private final PageRequest PAGE = PageRequest.of(0, 10);

        @BeforeEach
        void setUp() {
            given(activeLogQueryService.getSwipedCards(USER_ID)).willReturn(SwipedCards.empty());
            given(candidateService.rankCandidates(any(), any())).willReturn(List.of(
                    memberCard(1L, UUID.randomUUID()),
                    memberCard(2L, UUID.randomUUID()),
                    memberCard(3L, UUID.randomUUID())));
            given(candidateService.fetchNormalCards(anySet(), anySet(), anyInt())).willReturn(List.of(
                    memberCard(100L, UUID.randomUUID()),
                    memberCard(101L, UUID.randomUUID())));
        }

        @Test
        @DisplayName("startCardId가 존재하면 해당 카드가 첫 번째로 온다")
        void shouldPrependStartCard_whenStartCardExists() {
            MemberCardResult startCard = memberCard(99L, START_UUID);
            given(cardQueryService.findByPublicId(START_UUID)).willReturn(Optional.of(startCard));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PAGE, START_UUID).getContent();

            assertThat(result.getFirst().card().publicId()).isEqualTo(START_UUID);
        }

        @Test
        @DisplayName("startCardId가 이미 추천 목록에 있으면 중복 없이 첫 번째로 온다")
        void shouldNotDuplicate_whenStartCardAlreadyInResults() {
            MemberCardResult startCard = memberCard(1L, START_UUID);
            given(candidateService.rankCandidates(any(), any()))
                    .willReturn(List.of(startCard, memberCard(2L, UUID.randomUUID())));
            given(candidateService.fetchNormalCards(anySet(), anySet(), anyInt()))
                    .willReturn(List.of(memberCard(100L, UUID.randomUUID())));
            given(cardQueryService.findByPublicId(START_UUID)).willReturn(Optional.of(startCard));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PAGE, START_UUID).getContent();

            long count = result.stream()
                    .filter(r -> START_UUID.equals(r.card().publicId()))
                    .count();
            assertThat(count).isEqualTo(1);
            assertThat(result.getFirst().card().publicId()).isEqualTo(START_UUID);
        }

        @Test
        @DisplayName("startCardId에 해당하는 카드가 없으면 결과 목록 그대로 반환한다")
        void shouldReturnOriginalList_whenStartCardNotFound() {
            given(cardQueryService.findByPublicId(START_UUID)).willReturn(Optional.empty());

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, PAGE, START_UUID).getContent();

            assertThat(result.getFirst().card().publicId()).isNotEqualTo(START_UUID);
        }

        @Test
        @DisplayName("page > 0이면 startCardId가 있어도 prepend하지 않는다")
        void shouldNotPrepend_whenPageIsNotFirst() {
            PageRequest page1 = PageRequest.of(1, 10);
            given(candidateService.rankCandidates(any(), any())).willReturn(List.of(
                    memberCard(1L, UUID.randomUUID()), memberCard(2L, UUID.randomUUID()),
                    memberCard(3L, UUID.randomUUID()), memberCard(4L, UUID.randomUUID()),
                    memberCard(5L, UUID.randomUUID()), memberCard(6L, UUID.randomUUID()),
                    memberCard(7L, UUID.randomUUID()), memberCard(8L, UUID.randomUUID()),
                    memberCard(9L, UUID.randomUUID()), memberCard(10L, UUID.randomUUID()),
                    memberCard(11L, UUID.randomUUID()), memberCard(12L, UUID.randomUUID()),
                    memberCard(13L, UUID.randomUUID()), memberCard(14L, UUID.randomUUID()),
                    memberCard(15L, UUID.randomUUID()), memberCard(16L, UUID.randomUUID()),
                    memberCard(17L, UUID.randomUUID()), memberCard(18L, UUID.randomUUID()),
                    memberCard(19L, UUID.randomUUID()), memberCard(20L, UUID.randomUUID())));
            given(candidateService.fetchNormalCards(anySet(), anySet(), anyInt())).willReturn(List.of(
                    memberCard(100L, UUID.randomUUID()), memberCard(101L, UUID.randomUUID()),
                    memberCard(102L, UUID.randomUUID()), memberCard(103L, UUID.randomUUID()),
                    memberCard(104L, UUID.randomUUID()), memberCard(105L, UUID.randomUUID()),
                    memberCard(106L, UUID.randomUUID()), memberCard(107L, UUID.randomUUID())));

            List<RecommendCardResult> result =
                    recommendQueryService.getRecommendCards(USER_ID, page1, START_UUID).getContent();

            assertThat(result).noneMatch(r -> START_UUID.equals(r.card().publicId()));
        }
    }

    @Nested
    @DisplayName("비회원 SEO startCardId 처리")
    class GuestStartCard {

        private final UUID START_UUID = UUID.randomUUID();
        private final PageRequest PAGE = PageRequest.of(0, 10);

        @Test
        @DisplayName("startCardId가 존재하면 해당 카드가 첫 번째로 온다")
        void shouldPrependStartCard_whenStartCardExists() {
            GuestCardResult startCard = guestCard(START_UUID, "http://start.jpg");
            GuestCardResult other1 = guestCard(UUID.randomUUID(), "http://other1.jpg");
            GuestCardResult other2 = guestCard(UUID.randomUUID(), "http://other2.jpg");

            given(cardQueryService.getCardsForGuestRandom(PAGE))
                    .willReturn(new SliceImpl<>(List.of(other1, other2), PAGE, false));
            given(cardQueryService.findByPublicIdForGuest(START_UUID))
                    .willReturn(Optional.of(startCard));

            List<GuestCardResult> result = recommendQueryService.getGuestCards(PAGE, START_UUID).getContent();

            assertThat(result.getFirst().publicId()).isEqualTo(START_UUID);
        }

        @Test
        @DisplayName("startCardId가 랜덤 목록에 이미 있으면 중복 없이 첫 번째로 온다")
        void shouldNotDuplicate_whenStartCardAlreadyInRandomList() {
            GuestCardResult startCard = guestCard(START_UUID, "http://start.jpg");
            GuestCardResult other = guestCard(UUID.randomUUID(), "http://other.jpg");

            given(cardQueryService.getCardsForGuestRandom(PAGE))
                    .willReturn(new SliceImpl<>(List.of(startCard, other), PAGE, false));
            given(cardQueryService.findByPublicIdForGuest(START_UUID))
                    .willReturn(Optional.of(startCard));

            List<GuestCardResult> result = recommendQueryService.getGuestCards(PAGE, START_UUID).getContent();

            long count = result.stream().filter(r -> r.publicId().equals(START_UUID)).count();
            assertThat(count).isEqualTo(1);
            assertThat(result.getFirst().publicId()).isEqualTo(START_UUID);
        }

        @Test
        @DisplayName("startCardId에 해당하는 카드가 없으면 랜덤 목록 그대로 반환한다")
        void shouldReturnRandomList_whenStartCardNotFound() {
            GuestCardResult other = guestCard(UUID.randomUUID(), "http://other.jpg");

            given(cardQueryService.getCardsForGuestRandom(PAGE))
                    .willReturn(new SliceImpl<>(List.of(other), PAGE, false));
            given(cardQueryService.findByPublicIdForGuest(START_UUID))
                    .willReturn(Optional.empty());

            List<GuestCardResult> result = recommendQueryService.getGuestCards(PAGE, START_UUID).getContent();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().publicId()).isNotEqualTo(START_UUID);
        }

        @Test
        @DisplayName("startCardId가 null이면 랜덤 목록 그대로 반환한다")
        void shouldReturnRandomList_whenStartCardIdIsNull() {
            GuestCardResult card = guestCard(UUID.randomUUID(), "http://card.jpg");

            given(cardQueryService.getCardsForGuestRandom(PAGE))
                    .willReturn(new SliceImpl<>(List.of(card), PAGE, false));

            List<GuestCardResult> result = recommendQueryService.getGuestCards(PAGE, null).getContent();

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("결과가 pageSize를 초과하지 않는다")
        void shouldNotExceedPageSize_whenStartCardPrepended() {
            GuestCardResult startCard = guestCard(START_UUID, "http://start.jpg");
            List<GuestCardResult> tenOthers = List.of(
                    guestCard(UUID.randomUUID(), "http://1.jpg"),
                    guestCard(UUID.randomUUID(), "http://2.jpg"),
                    guestCard(UUID.randomUUID(), "http://3.jpg"),
                    guestCard(UUID.randomUUID(), "http://4.jpg"),
                    guestCard(UUID.randomUUID(), "http://5.jpg"),
                    guestCard(UUID.randomUUID(), "http://6.jpg"),
                    guestCard(UUID.randomUUID(), "http://7.jpg"),
                    guestCard(UUID.randomUUID(), "http://8.jpg"),
                    guestCard(UUID.randomUUID(), "http://9.jpg"),
                    guestCard(UUID.randomUUID(), "http://10.jpg"));

            given(cardQueryService.getCardsForGuestRandom(PAGE))
                    .willReturn(new SliceImpl<>(tenOthers, PAGE, false));
            given(cardQueryService.findByPublicIdForGuest(START_UUID))
                    .willReturn(Optional.of(startCard));

            List<GuestCardResult> result = recommendQueryService.getGuestCards(PAGE, START_UUID).getContent();

            assertThat(result).hasSize(PAGE.getPageSize());
            assertThat(result.getFirst().publicId()).isEqualTo(START_UUID);
        }

        private GuestCardResult guestCard(UUID publicId, String imageUrl) {
            return new GuestCardResult(1L, publicId, imageUrl, null, null, List.of());
        }
    }

    private List<RecommendCardResult> recommendedOnly(List<RecommendCardResult> result) {
        return result.stream().filter(RecommendCardResult::recommended).toList();
    }

    private List<RecommendCardResult> normalOnly(List<RecommendCardResult> result) {
        return result.stream().filter(r -> !r.recommended()).toList();
    }

    private List<MemberCardResult> memberCards(Long... ids) {
        return java.util.Arrays.stream(ids)
                .map(id -> new MemberCardResult(id, null, null, null, null, List.of(), List.of()))
                .toList();
    }

    private MemberCardResult memberCard(Long id, UUID publicId) {
        return new MemberCardResult(id, publicId, null, null, null, List.of(), List.of());
    }
}
