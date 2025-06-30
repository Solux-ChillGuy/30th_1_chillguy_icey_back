package com.project.icey.app.dto;


import com.project.icey.app.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> user.getRoleType().name());
        return collection;
    }


    @Override
    public String getUsername() { return user.getEmail(); } // 이메일을 반환하도록 수정

    public String getRole() { return user.getRoleType().name(); }


    public User getUser(){
        return user;
    }

    @Override
    public String getPassword() {
        return ""; // 또는 null userDetails implements 할 경우 필수로 구현해야함
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
