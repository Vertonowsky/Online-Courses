package com.example.vertonowsky.security.service;

import com.example.vertonowsky.avatar.AvatarSerializer;
import com.example.vertonowsky.security.RegistrationMethod;
import com.example.vertonowsky.security.model.CustomUserDetails;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    /*
        This class represents all users who want to login using traditional method. They own a password, that's why they have a property
        named 'registrationMethod' with a value equal to RegistrationMethod.DEFAULT.

        When traditional validation is being proceeded, Spring Security only looks after users with 'registrationMethod' = RegistrationMethod.DEFAULT.

        This class is responsible for finding those users and returning their UserDetails instance,

     */

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email, RegistrationMethod.DEFAULT);

        user.orElseThrow(() -> new UsernameNotFoundException("Błędny adres email lub hasło!"));

        return user.map((u) -> new CustomUserDetails(u, AvatarSerializer.serialize(u.getAvatar()))).get();
    }

}
