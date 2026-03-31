package com.dekk.app.user.application;

import com.dekk.app.user.application.command.UserOnboardingCommand;
import com.dekk.app.user.application.command.UserProfileUpdateCommand;
import com.dekk.app.user.domain.exception.UserBusinessException;
import com.dekk.app.user.domain.exception.UserErrorCode;
import com.dekk.app.user.domain.model.User;
import com.dekk.app.user.domain.repository.ProfileRepository;
import com.dekk.app.user.domain.repository.UserRepository;
import com.dekk.global.event.UserDeletedEvent;
import com.dekk.global.event.UserOnboardedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void onboardUser(Long userId, UserOnboardingCommand command) {
        User user = getUser(userId);
        validateDuplicateNickname(command.nickname());

        user.completeOnboarding(command);

        eventPublisher.publishEvent(UserOnboardedEvent.of(user.getId()));
    }

    public void updateProfileInfo(Long userId, UserProfileUpdateCommand command) {
        User user = getUserWithProfile(userId);
        if (isNicknameChanged(user, command.nickname())) {
            validateDuplicateNickname(command.nickname());
        }
        user.updateProfileInfo(command);
    }

    public void deleteUser(Long userId) {
        eventPublisher.publishEvent(UserDeletedEvent.of(userId));

        User user = getUser(userId);
        user.deleteUser();
    }

    private void validateDuplicateNickname(String nickname) {
        if (profileRepository.existsByNickname(nickname)) {
            throw new UserBusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private boolean isNicknameChanged(User user, String newNickname) {
        return newNickname != null && !newNickname.equals(user.getProfile().getNickname());
    }

    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserBusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private User getUserWithProfile(Long userId) {
        return userRepository
                .findWithProfileById(userId)
                .orElseThrow(() -> new UserBusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
