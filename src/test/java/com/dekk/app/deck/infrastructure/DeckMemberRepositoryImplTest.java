package com.dekk.app.deck.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.dekk.app.deck.domain.model.DeckMember;
import com.dekk.app.deck.domain.model.enums.DeckRole;
import com.dekk.app.deck.infrastructure.DeckMemberRepositoryImpl;
import com.dekk.app.deck.infrastructure.jpa.DeckMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeckMemberRepositoryImplTest {

    @InjectMocks
    private DeckMemberRepositoryImpl deckMemberRepositoryImpl;

    @Mock
    private DeckMemberJpaRepository jpaRepository;

    private final Long deckId = 1L;
    private final Long userId = 100L;
    private final DeckRole role = DeckRole.GUEST;

    @Test
    @DisplayName("이전에 참여 후 퇴장(Soft Delete)한 이력이 있다면, 재활성화 업데이트 쿼리가 실행되고 새로 save하지 않는다")
    void reactivateOrSave_reactivates_whenDeletedHistoryExists() {
        given(jpaRepository.reactivateIfDeleted(deckId, userId, role.name())).willReturn(1);

        deckMemberRepositoryImpl.reactivateOrSave(deckId, userId, role);

        verify(jpaRepository, never()).save(any(DeckMember.class));
    }

    @Test
    @DisplayName("참여 이력이 없다면 재활성화 업데이트 결과가 0이므로, 새로운 DeckMember 엔티티를 save한다")
    void reactivateOrSave_saves_whenNoDeletedHistoryExists() {
        given(jpaRepository.reactivateIfDeleted(deckId, userId, role.name())).willReturn(0);

        deckMemberRepositoryImpl.reactivateOrSave(deckId, userId, role);

        verify(jpaRepository).save(any(DeckMember.class));
    }
}
