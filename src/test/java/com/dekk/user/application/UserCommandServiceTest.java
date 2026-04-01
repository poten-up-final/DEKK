package com.dekk.user.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.dekk.app.user.application.UserCommandService;
import com.dekk.app.user.domain.model.User;
import com.dekk.app.user.domain.repository.ProfileRepository;
import com.dekk.app.user.domain.repository.UserRepository;
import com.dekk.global.event.UserDeletedEvent;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @InjectMocks
    private UserCommandService userCommandService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("유저 탈퇴 시 연관 데이터를 정리하는 글로벌 이벤트를 발행한 후 유저 상태를 DELETED로 변경한다")
    void deleteUser_success() {
        Long userId = 1L;
        User user = mock(User.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        userCommandService.deleteUser(userId);

        verify(eventPublisher).publishEvent(any(UserDeletedEvent.class));
        verify(user).deleteUser();
    }
}
