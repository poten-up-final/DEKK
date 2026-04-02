package com.dekk.global.security.oauth2;

import com.dekk.app.user.domain.model.enums.UserStatus;
import com.dekk.global.security.jwt.JwtPrincipal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomUserDetails implements UserDetails, OAuth2User, JwtPrincipal {

    @Getter
    private final Long id;

    private final String email;
    private final String role;

    @Getter
    private final UserStatus status;

    private final Map<String, Object> attributes;

    public CustomUserDetails(Long id, String email, String role, UserStatus status, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.status = status;
        this.attributes = attributes;
    }

    public CustomUserDetails(Long id, String email, String role, UserStatus status) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.status = status;
        this.attributes = Collections.emptyMap();
    }

    @Override
    public Long getJwtId() {
        return this.id;
    }

    @Override
    public String getJwtEmail() {
        return this.email;
    }

    @Override
    public String getJwtRole() {
        return this.role;
    }

    @Override
    public String getJwtStatus() {
        return this.status != null ? this.status.name() : null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
