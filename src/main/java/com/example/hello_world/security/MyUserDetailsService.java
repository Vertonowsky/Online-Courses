package com.example.hello_world.security;

import com.example.hello_world.RegistrationMethod;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    /*
        This class represents all users who want to login using traditional method. They own a password, that's why they have a property
        named 'registrationMethod' with a value equal to RegistrationMethod.DEFAULT.

        When traditional validation is being proceeded, Spring Security only looks after users with 'registrationMethod' = RegistrationMethod.DEFAULT.

        This class is responsible for finding those users and returning their UserDetails instance,

     */


    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email, RegistrationMethod.DEFAULT);

        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));

        return user.map(CustomUserDetails::new).get();
    }


}
