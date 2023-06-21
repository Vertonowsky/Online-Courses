package com.example.hello_world.security;

import com.example.hello_world.persistence.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public interface AuthenticatedUser {

    String getEmail();

    String getPassword();

    List<GrantedAuthority> getAuthorities();

    boolean isEnabled();


    default List<GrantedAuthority> getAuthorities2(List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles)
            authorities.add(new SimpleGrantedAuthority(role.getRoleType().toString()));

        return authorities;
    }

}
