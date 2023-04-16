package com.example.hello_world.web.service;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.VerificationToken;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.VerificationTokenRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Transactional
public class UserService implements IUserService {

    @Value("${verification.email.url}")
    private String verificationEmailUrl;

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
        createTokenAndSendEmail(user);
    }



    public void verifyUserEmail(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        // Check if token UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(tokenUuid);
        if (!uuidMatcher.matches()) throw new InvalidUUIDFormatException("Nieprawidłowy token.");

        //Check if token exists
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(UUID.fromString(tokenUuid));
        if (verificationToken.isEmpty()) throw new TokenNotFoundException("Nie odnaleziono tokenu.");

        VerificationToken token = verificationToken.get();
        if (!token.isValid() || token.getExpiryDate().getTime() <= System.currentTimeMillis()) throw new TokenExpiredException("Wskazany token utracił swoją ważność.");

        token.setValid(false);
        User user = token.getUser();
        user.setVerified(true);
        verificationTokenRepository.save(token);
        userRepository.save(user);
    }




    public void resendEmail(String email) throws InvalidEmailFormatException, UserNotFoundException, TokenExpiredException, LowDelayException {
        if (!UserDto.isEmailValid(email))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("Nie odnaleziono konta powiązanego z adresem %s", email));

        // Check is account isn't already verified
        if (user.get().isVerified()) throw new TokenExpiredException("Konto zostało już aktywowane.");


        // Check if user has recently sent request for resending email
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUserAndValid(user.get(), true);
        if (verificationToken.isPresent()) {
            Date now = new Date(System.currentTimeMillis());
            long duration = (now.getTime() - verificationToken.get().getCreationDate().getTime()) / 1000;
            // If delay is smaller than 120 seconds
            if (duration < 120) throw new LowDelayException(String.format("Odczekaj %d sekund!", 120 - duration));
        }


        // Set already awaiting verification tokens invalid
        disableOldTokens(user.get());

        // Create new verification token
        createTokenAndSendEmail(user.get());
    }




    private void disableOldTokens(User user) {
        List<VerificationToken> verificationTokens = verificationTokenRepository.findAllByUser(user);
        if (verificationTokens.size() > 0) {
            for(VerificationToken token : verificationTokens)
                token.setValid(false);
            verificationTokenRepository.saveAll(verificationTokens);
        }
    }



    private void createTokenAndSendEmail(User user) {
        VerificationToken token = new VerificationToken(new Date(System.currentTimeMillis()));
        token.setUser(user);
        verificationTokenRepository.save(token);


        // Send verification email
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("to", user.getEmail());
        variables.put("url", verificationEmailUrl + "/auth/verify?token=" + token.getToken().toString());

        emailService.sendVerificationEmail(user.getEmail(), "Potwierdź swoją rejestrację - Kursowo.pl", variables, "templates/test-email.html");
    }



    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}

