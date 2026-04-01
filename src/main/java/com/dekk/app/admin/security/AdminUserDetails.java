package com.dekk.app.admin.security;

import com.dekk.app.user.domain.model.enums.UserStatus;
import com.dekk.global.security.jwt.JwtPrincipal;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record AdminUserDetails(Long adminId, String email, String role) implements UserDetails, JwtPrincipal {

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Long getJwtId() {
        return this.adminId;
    }

    @Override
    public String getJwtEmail() {
        return this.email;
    }

    @Override
    public String getJwtRole() {
        return ROLE_PREFIX + this.role;
    }

    @Override
    public UserStatus getJwtStatus() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(ROLE_PREFIX + this.role));
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
