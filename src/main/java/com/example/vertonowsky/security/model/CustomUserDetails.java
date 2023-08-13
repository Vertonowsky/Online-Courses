package com.example.vertonowsky.security.model;

import com.example.vertonowsky.avatar.AvatarDto;
import com.example.vertonowsky.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails, AuthenticatedUser, Serializable {

    private final String email;
    private final String password;
    private final boolean verified;
    private AvatarDto avatar;
    private final List<GrantedAuthority> authorities;


    public CustomUserDetails(User user, AvatarDto avatar) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.verified = user.isVerified();
        this.authorities = getAuthorities(Collections.singletonList(user.getRole()));
        this.avatar = avatar;
    }


    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAvatar(AvatarDto avatar) {
        this.avatar = avatar;
    }

    @Override
    public AvatarDto getAvatar() {
        return avatar;
    }

    @Override
    public String getEmail() { return email; }

    @Override
    public String getPassword() {
        return password;
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
        return verified;
    }

}
