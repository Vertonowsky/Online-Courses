package com.example.hello_world.web.service;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.VerificationToken;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.VerificationTokenRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;


@Service
@Transactional
public class UserService implements IUserService {

    private UserRepository userRepository;

    private VerificationTokenRepository verificationTokenRepository;

    private EmailService emailService;


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setVerificationTokenRepository(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }




    @Override
    public void registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException, InvalidEmailFormatException, InvalidPasswordFormatException, PasswordsNotEqualException, TermsNotAcceptedException {
        if (emailExists(userDto.getEmail()))
            throw new UserAlreadyExistsException(String.format("Istnieje już konto powiązane z adresem: %s!", userDto.getEmail()));

        if (!userDto.isEmailValid())
            throw new InvalidEmailFormatException("Nieprawidłowy formatu adresu e-mail.");

        if (!userDto.isPasswordValid())
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


        // Make old token invalid
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByUserAndValid(user, true);
        if (optionalToken.isPresent()) {
            VerificationToken tok = optionalToken.get();
            tok.setValid(false);
            verificationTokenRepository.save(tok);
        }


        // Create verification token
        Date now = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        VerificationToken token = new VerificationToken(calendar.getTime());
        token.setUser(user);
        verificationTokenRepository.save(token);


        // Send verification email
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("to", user.getEmail());
        variables.put("url", "http://localhost:8080/rejestracja/weryfikacja&token=" + token.getToken().toString());

        emailService.sendVerificationEmail(user.getEmail(), "Potwierdź swoją rejestrację - Kursowo.pl", variables, "templates/test-email.html");

    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}

