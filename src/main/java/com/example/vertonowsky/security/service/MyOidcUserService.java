package com.example.vertonowsky.security.service;

import com.example.vertonowsky.security.RegistrationMethod;
import com.example.vertonowsky.security.model.CustomOidcUser;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserDto;
import com.example.vertonowsky.user.UserRepository;
import com.example.vertonowsky.user.service.IUserService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class MyOidcUserService extends OidcUserService {

    private final IUserService userService;
    private final UserRepository userRepository;

    public MyOidcUserService(IUserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }


    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        CustomOidcUser googleUser = new CustomOidcUser(oidcUser);

        try {

            userService.registerNewUserAccount(new UserDto(googleUser.getEmail(), null, null, true, RegistrationMethod.GOOGLE));

        } catch (Exception e) {
            System.out.println("Uzytkownik jest juz zarejestrowany. Przystepuje do logowania.");
        }

        Optional<User> user = userRepository.findByEmail(googleUser.getEmail());
        if (user.isPresent()) {
            googleUser.setVerified(user.get().isVerified());
            googleUser.setAuthorities(Collections.singletonList(user.get().getRole()));
            //googleUser.setAvatar(user.get().getAvatar());
        }

        return googleUser;
    }

}
