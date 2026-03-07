package com.dekk.auth.application;

import com.dekk.auth.application.command.TokenRefreshCommand;
import com.dekk.auth.application.dto.result.TokenRefreshResult;
import com.dekk.auth.domain.exception.AuthBusinessException;
import com.dekk.auth.domain.exception.AuthErrorCode;
import com.dekk.auth.domain.model.BlacklistAccessToken;
import com.dekk.auth.domain.repository.BlacklistRepository;
import com.dekk.auth.domain.repository.RefreshTokenRepository;
import com.dekk.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistRepository blacklistRepository;

    public TokenRefreshResult refreshToken(TokenRefreshCommand command) {
        jwtTokenProvider.validateToken(command.refreshToken());

        if(!jwtTokenProvider.isRefreshToken(command.refreshToken())) {
            throw new AuthBusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication((command.refreshToken()));

        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return new TokenRefreshResult(newAccessToken, newRefreshToken);
    }

    public void logout(Long UserId, String accessToken) {
        refreshTokenRepository.deleteByUserId(UserId);

        long remainingExpirationTime = jwtTokenProvider.getRemainingExpirationTime(accessToken);

        if (remainingExpirationTime > 0) {
            BlacklistAccessToken blacklistAccessToken = BlacklistAccessToken.create(accessToken);
            blacklistRepository.save(blacklistAccessToken, remainingExpirationTime);
        }
    }
}
