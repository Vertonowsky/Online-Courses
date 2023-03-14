package com.example.hello_world.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class GoogleUserInfo implements OAuth2User {

    private Map<String, Object> attributes;

    private String email;
    private boolean active;

    //private List<GrantedAuthority> authorities;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.email = (String) attributes.get("email");
        this.active = true;
    }

    public String getId() {
        return (String) attributes.get("sub");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}