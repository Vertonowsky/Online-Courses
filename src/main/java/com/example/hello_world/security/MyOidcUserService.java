package com.example.hello_world.security;

import com.example.hello_world.RegistrationMethod;
import com.example.hello_world.validation.UserAlreadyExistsException;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class MyOidcUserService extends OidcUserService {

    @Autowired
    private IUserService userService;


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }


    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        GoogleUserInfo googleUser = new GoogleUserInfo(oidcUser.getAttributes());

        try {
            userService.registerNewUserAccount(new UserDto(googleUser.getEmail(), null, null, true, RegistrationMethod.GOOGLE));

        } catch (UserAlreadyExistsException uaeEx) {
            System.out.println("Uzytkownik jest juz zarejestrowany. Przystepuje do logowania.");
        }

        // see what other data from userRequest or oidcUser you need

        return oidcUser;
    }

}