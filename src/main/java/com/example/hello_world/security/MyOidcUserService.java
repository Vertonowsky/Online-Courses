package com.example.hello_world.security;

import com.example.hello_world.RegistrationMethod;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyOidcUserService extends OidcUserService {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

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
            googleUser.setAuthorities(user.get().getRoles());
        }

        return googleUser;
    }

}
