package com.example.hello_world.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface AuthenticatedUser {

    String getEmail();

    String getPassword();

    List<GrantedAuthority> getAuthorities();

    boolean isVerified();

}
