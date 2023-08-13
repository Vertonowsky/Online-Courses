package com.example.vertonowsky.security.model;

import com.example.vertonowsky.avatar.AvatarDto;
import com.example.vertonowsky.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOidcUser implements OidcUser, AuthenticatedUser, Serializable {


    private final OidcUser oidcUser;
    private final String email;
    private boolean verified;
    private AvatarDto avatar;
    private List<GrantedAuthority> authorities;


    public CustomOidcUser(OidcUser oidcUser) {
        this.oidcUser= oidcUser;
        this.email = (String) oidcUser.getAttributes() .get("email");
        this.verified = true;
    }

    public void setVerified(boolean verified) { this.verified = verified; }

    public void setAuthorities(List<Role> roles) {
        this.authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().toString())).collect(Collectors.toList());
    }

    public void setAvatar(AvatarDto avatar) {
        this.avatar = avatar;
    }

    @Override
    public AvatarDto getAvatar() {
        return avatar;
    }

    @Override
    public String getPassword() { return null; }

    @Override
    public <A> A getAttribute(String name) {
        return oidcUser.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
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

    @Override
    public String getName() {
        return null;
    }
}