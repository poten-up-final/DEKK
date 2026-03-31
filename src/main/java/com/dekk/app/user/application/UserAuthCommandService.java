package com.dekk.app.user.application;

import com.dekk.app.user.application.command.UserCreateCommand;
import com.dekk.app.user.application.dto.result.UserAuthResult;
import com.dekk.app.user.domain.model.User;
import com.dekk.app.user.domain.model.enums.Provider;
import com.dekk.app.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthCommandService {

    private final UserRepository userRepository;

    @Transactional
    public UserAuthResult getOrRegisterUser(String email, Provider provider, String providerId) {
        Optional<User> user = userRepository.findByProviderAndProviderId(provider, providerId);
        if (user.isPresent()) {
            return UserAuthResult.from(user.get());
        }

        User newUser = userRepository.save(User.create(new UserCreateCommand(email, provider, providerId)));

        return UserAuthResult.from(newUser);
    }
}
