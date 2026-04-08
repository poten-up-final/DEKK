package com.dekk.global.security.jwt;

import com.dekk.app.admin.security.AdminUserDetails;
import com.dekk.app.auth.domain.exception.AuthBusinessException;
import com.dekk.app.auth.domain.exception.AuthErrorCode;
import com.dekk.app.user.domain.model.enums.UserStatus;
import com.dekk.global.security.oauth2.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_TYPE_KEY = "type";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_STATUS = "status";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private final Key key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityTime = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityTime = refreshTokenValidityInSeconds * 1000;
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenValidityTime, ACCESS_TOKEN_TYPE);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenValidityTime, REFRESH_TOKEN_TYPE);
    }

    private String createToken(Authentication authentication, long tokenValidTime, String tokenType) {
        if (!(authentication.getPrincipal() instanceof JwtPrincipal principal)) {
            throw new AuthBusinessException(AuthErrorCode.INVALID_TOKEN);
        }

        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidTime);

        JwtBuilder builder = Jwts.builder()
                .setSubject(principal.getJwtEmail())
                .claim(AUTHORITIES_KEY, principal.getJwtRole())
                .claim(CLAIM_USER_ID, principal.getJwtId())
                .claim(TOKEN_TYPE_KEY, tokenType);

        if (principal.getJwtStatus() != null) {
            builder.claim(CLAIM_STATUS, principal.getJwtStatus());
        }

        return builder.signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    public boolean isAccessToken(String token) {
        String type = getClaims(token).get(TOKEN_TYPE_KEY, String.class);
        return ACCESS_TOKEN_TYPE.equals(type);
    }

    public boolean isRefreshToken(String token) {
        String type = getClaims(token).get(TOKEN_TYPE_KEY, String.class);
        return REFRESH_TOKEN_TYPE.equals(type);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String role = claims.get(AUTHORITIES_KEY).toString();

        Collection<? extends GrantedAuthority> authorities =
                java.util.Collections.singletonList(new SimpleGrantedAuthority(role));

        String email = claims.getSubject();
        Long id = ((Number) claims.get(CLAIM_USER_ID)).longValue();

        Object principal;

        if ("ROLE_ADMIN".equals(role) || "ROLE_SUPER_ADMIN".equals(role)) {
            principal = new AdminUserDetails(id, email, role);
        } else {
            String statusStr = claims.get(CLAIM_STATUS, String.class);
            UserStatus status = statusStr != null ? UserStatus.valueOf(statusStr) : UserStatus.ACTIVE;
            principal = new CustomUserDetails(id, email, role, status);
        }

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException | UnsupportedJwtException e) {
            throw new AuthBusinessException(AuthErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthBusinessException(AuthErrorCode.EXPIRED_TOKEN);
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getRemainingExpiration(String token) {
        try {
            Claims claims = getClaims(token);
            long expiration = claims.getExpiration().getTime();
            long now = new Date().getTime();
            return Math.max(0, (expiration - now) / 1000);
        } catch (Exception e) {
            return 0;
        }
    }
}
