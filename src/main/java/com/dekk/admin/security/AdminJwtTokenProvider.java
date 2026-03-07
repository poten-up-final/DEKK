package com.dekk.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class AdminJwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;

    public AdminJwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.admin-access-token-validity-in-seconds:3600}") long accessTokenValidityInSeconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
    }

    public String createAccessToken(AdminUserDetails adminUserDetails) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(adminUserDetails.email())
                .claim("adminId", adminUserDetails.adminId())
                .claim("role", adminUserDetails.role())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long adminId = claims.get("adminId", Long.class);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        AdminUserDetails principal = new AdminUserDetails(adminId, email, role);
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid Admin JWT token: {}", e.getMessage());
            return false;
        }
    }
}
