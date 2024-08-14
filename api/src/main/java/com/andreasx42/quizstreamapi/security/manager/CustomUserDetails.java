package com.andreasx42.quizstreamapi.security.manager;

import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public class CustomUserDetails extends User {
    private final Long id;

    public CustomUserDetails(Long id, String username, String password,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public CustomUserDetails(Long id, String username, String password,
            boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled,
                accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }
}
