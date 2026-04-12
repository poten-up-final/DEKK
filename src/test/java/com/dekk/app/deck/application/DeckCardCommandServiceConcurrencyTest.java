package com.dekk.app.deck.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dekk.app.deck.application.DeckCardCommandService;
import com.dekk.global.error.BusinessException;
import com.dekk.global.error.GlobalErrorCode;
import com.dekk.app.deck.domain.model.Deck;
import com.dekk.app.deck.domain.model.DeckMember;
import com.dekk.app.deck.domain.model.enums.DeckRole;
import com.dekk.app.deck.domain.repository.DeckCardRepository;
import com.dekk.app.deck.domain.repository.DeckMemberRepository;
import com.dekk.app.deck.domain.repository.DeckRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles("test")
class DeckCardCommandServiceConcurrencyTest {

    @Autowired
    private DeckCardCommandService deckCardCommandService;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private DeckMemberRepository deckMemberRepository;

    @Autowired
    private DeckCardRepository deckCardRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long userId = 1L;
    private Long customDeckId;
    private Long targetCardId = 100L;

    @BeforeEach
    void setUp() {
        Deck customDeck = Deck.createCustom(userId, "여름 코디 모음");
        deckRepository.save(customDeck);
        customDeckId = customDeck.getId();

        DeckMember hostMember = DeckMember.create(customDeckId, userId, DeckRole.HOST);
        deckMemberRepository.save(hostMember);
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.executeWithoutResult(status -> {
            deckCardRepository.deleteAllByDeckId(customDeckId);
            deckMemberRepository.deleteAllByDeckId(customDeckId);
            deckRepository.findById(customDeckId).ifPresent(deckRepository::delete);
        });
    }

    @Test
    @DisplayName("동시에 100번의 동일한 카드 저장 요청이 오면, 분산 락을 통해 1장만 실제 저장되고 나머지는 무시되거나 락 획득에 실패한다")
    void saveCardToCustomDeck_concurrency_test() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successOrIgnoredCount = new AtomicInteger();
        AtomicInteger lockExceptionCount = new AtomicInteger();

        try {
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        deckCardCommandService.saveToCustomDeck(userId, customDeckId, targetCardId);
                        successOrIgnoredCount.incrementAndGet();
                    } catch (BusinessException e) {
                        if (e.errorCode() == GlobalErrorCode.LOCK_ACQUISITION_FAILED) {
                            lockExceptionCount.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

        } finally {
            executorService.shutdown();
        }

        long savedCardCount = deckCardRepository.countByDeckId(customDeckId);
        assertThat(savedCardCount).isEqualTo(1L);

        assertThat(successOrIgnoredCount.get() + lockExceptionCount.get()).isEqualTo(100);

        System.out.println("정상 처리 횟수 (최초 1회 저장 + 나머지 중복 무시): " + successOrIgnoredCount.get());
        System.out.println("락 획득 실패(LOCK_ACQUISITION_FAILED) 횟수: " + lockExceptionCount.get());
    }
}
