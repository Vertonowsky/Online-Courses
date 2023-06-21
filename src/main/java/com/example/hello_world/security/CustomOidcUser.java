package com.example.hello_world.security;

import com.example.hello_world.persistence.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOidcUser implements OidcUser, AuthenticatedUser {

    private final Map<String, Object> attributes;
    private final OidcUser oidcUser;
    private final String email;
    private boolean verified;
    private List<GrantedAuthority> authorities;


    public CustomOidcUser(OidcUser oidcUser) {
        this.oidcUser   = oidcUser;
        this.attributes = oidcUser.getAttributes();
        this.email      = (String) attributes.get("email");
        this.verified   = true;
    }


    public String getId() {
        return (String) attributes.get("sub");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return email;
    }


    public void setAuthorities(List<Role> roles) {
        this.authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().toString())).collect(Collectors.toList());

//        this.authorities = Arrays.stream(roles.split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
    }

    public void setVerified(boolean verified) { this.verified = verified; }

    @Override
    public String getPassword() { return null; }

    @Override
    public <A> A getAttribute(String name) {
        return oidcUser.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() { return verified; }

    @Override
    public Map<String, Object> getClaims() { return oidcUser.getClaims(); }

    @Override
    public OidcUserInfo getUserInfo() { return oidcUser.getUserInfo(); }

    @Override
    public OidcIdToken getIdToken() { return oidcUser.getIdToken(); }
}