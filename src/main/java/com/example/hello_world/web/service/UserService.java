package com.example.hello_world.web.service;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.UserDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;


    public UserService(UserRepository userRepository, VerificationTokenService verificationTokenService) {
        this.userRepository = userRepository;
        this.verificationTokenService = verificationTokenService;
    }




    @Override
    public void registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException, InvalidEmailFormatException, InvalidPasswordFormatException, PasswordsNotEqualException, TermsNotAcceptedException {
        if (emailExists(userDto.getEmail()))
            throw new UserAlreadyExistsException(String.format("Konto o adresie %s już istnieje!", userDto.getEmail()));

        if (!UserDto.isEmailValid(userDto.getEmail()))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        if (!UserDto.isPasswordValid(userDto.getPassword()))
            throw new InvalidPasswordFormatException("Hasło nie spełnia wymagań bezpieczeństwa.");

        if (!userDto.arePasswordsEqual())
            throw new PasswordsNotEqualException("Podane hasła nie są identyczne.");

        if (!userDto.areTermsChecked())
            throw new TermsNotAcceptedException("Wymagana jest akceptacja regulaminu.");


        User user = new User();
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null) {
            user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));  //encrypt password with BCrypt
            user.setVerified(false);
        } else {
            user.setPassword(null);
            user.setVerified(true);
        }
        user.setRoles("ROLE_USER");
        user.setRegistrationDate(new Date(System.currentTimeMillis()));
        user.setRegistrationMethod(userDto.getRegistrationMethod());
        userRepository.save(user);

        // Create new verification token
        verificationTokenService.createTokenAndSendEmail(user);
    }


    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}

