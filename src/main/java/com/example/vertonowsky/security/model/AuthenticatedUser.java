package com.example.vertonowsky.security.model;

import com.example.vertonowsky.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public interface AuthenticatedUser {

    String getEmail();

    String getPassword();

    boolean isEnabled();


    /**
     * Cast user's authorities into List<GrantedAuthority> basing on his role
     *
     * @param roles List of user's role. Current version of application supports only one role. List is a futuristic implementation
     * @return List<GrantedAuthority> containing different RoleTypes
     */
    default List<GrantedAuthority> getAuthorities(List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles)
            authorities.add(new SimpleGrantedAuthority(role.getRoleType().toString()));

        return authorities;
    }

}
