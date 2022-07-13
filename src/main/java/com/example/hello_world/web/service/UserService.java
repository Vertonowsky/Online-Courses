package com.example.hello_world.web.service;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.validation.UserAlreadyExistsException;
import com.example.hello_world.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistsException("There is an account with that email address: "
                    + userDto.getEmail());
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));  //encrypt password with BCrypt
        user.setActive(true);
        user.setRoles("ROLE_USER");
        user.setRegistrationDate(new Date(System.currentTimeMillis()));

        return userRepository.save(user);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}

