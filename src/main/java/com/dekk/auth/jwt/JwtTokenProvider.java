package com.dekk.auth.jwt;

import com.dekk.auth.domain.exception.AuthBusinessException;
import com.dekk.auth.domain.exception.AuthErrorCode;
import com.dekk.security.oauth2.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
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
        return createToken(authentication, accessTokenValidityTime);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenValidityTime);
    }

    private String createToken(Authentication authentication, long tokenValidTime) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Long userId = userDetails.getId();

        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidTime);

        return Jwts.builder()
                .setSubject(email)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String authorities = claims.get(AUTHORITIES_KEY).toString();
        Collection<? extends GrantedAuthority> authorityList =
                Arrays.stream(authorities.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        String email = claims.getSubject();
        Long userId = ((Number) claims.get("userId")).longValue();

        CustomUserDetails principal = new CustomUserDetails(userId, email, authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorityList);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new AuthBusinessException(AuthErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthBusinessException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new AuthBusinessException(AuthErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new AuthBusinessException(AuthErrorCode.EMPTY_CLAIMS);
        }
    }
}
