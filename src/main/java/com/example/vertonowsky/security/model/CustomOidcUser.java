package com.example.vertonowsky.security.model;

import com.example.vertonowsky.avatar.AvatarDto;
import com.example.vertonowsky.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOidcUser implements OidcUser, AuthenticatedUser {

    private final String email;
    private boolean verified;
    private AvatarDto avatar;
    private List<GrantedAuthority> authorities;
    private String id;
    private String name;


    public CustomOidcUser(OidcUser oidcUser) {
        Map<String, Object> attributes = oidcUser.getAttributes();
        this.email = (String) attributes.get("email");
        this.verified = true;
        this.id = (String) attributes.get("sub");
        this.name = (String) attributes.get("name");
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setAvatar(AvatarDto avatar) {
        this.avatar = avatar;
    }

    public void setVerified(boolean verified) { this.verified = verified; }

    @Override
    public AvatarDto getAvatar() {
        return avatar;
    }

    @Override
    public String getPassword() { return null; }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() { return verified; }

    @Override
    public Map<String, Object> getClaims() { return null; }

    @Override
    public OidcUserInfo getUserInfo() { return null; }

    @Override
    public OidcIdToken getIdToken() { return null; }
}