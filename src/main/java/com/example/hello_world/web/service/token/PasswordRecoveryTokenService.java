package com.example.hello_world.web.service.token;

import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.PasswordRecoveryToken;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.token.PasswordRecoveryTokenRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PasswordRecoveryTokenService {

    @Value("${verification.email.url}")
    private String verificationEmailUrl;
    private final UserRepository userRepository;
    private final PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;
    private final EmailService emailService;




    public PasswordRecoveryTokenService(UserRepository userRepository, PasswordRecoveryTokenRepository passwordRecoveryTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordRecoveryTokenRepository = passwordRecoveryTokenRepository;
        this.emailService = emailService;
    }



    public void createPasswordRecoveryToken(String email) throws InvalidEmailFormatException, UserNotFoundException, LowDelayException {
        if (!Regex.EMAIL_PATTERN.matches(email))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("Nie odnaleziono konta o adresie email %s", email));


        // TODO check if user isn't currently changing his password


        // Check if user has recently sent request for resending email
        Optional<PasswordRecoveryToken> token = passwordRecoveryTokenRepository.findByUserAndValid(user.get(), true);
        if (token.isPresent()) {
            Date now = new Date(System.currentTimeMillis());
            long duration = (now.getTime() - token.get().getCreationDate().getTime()) / 1000;
            // If delay is smaller than 120 seconds
            if (duration < 120) throw new LowDelayException(String.format("Odczekaj %d sekund!", 120 - duration));
        }


        // Set already awaiting verification tokens invalid
        //disableOldTokens(user.get());

        // Create new verification token
        createTokenAndSendEmail(user.get());
    }




    private void createTokenAndSendEmail(User user) {
        PasswordRecoveryToken token = new PasswordRecoveryToken();
        token.setUser(user);
        passwordRecoveryTokenRepository.save(token);


        // Send verification email
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("to", user.getEmail());
        variables.put("url", verificationEmailUrl + "/auth/verify?token=" + token.getToken().toString());

        emailService.sendVerificationEmail(user.getEmail(), "Potwierdź swoją rejestrację - Kursowo.pl", variables, "templates/test-email.html");
    }





    public void verifyUserEmail(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException {
        // Check if token UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(tokenUuid)) throw new InvalidUUIDFormatException("Nieprawidłowy token.");

        //Check if token exists
        Optional<PasswordRecoveryToken> optionalToken = passwordRecoveryTokenRepository.findByToken(UUID.fromString(tokenUuid));
        if (optionalToken.isEmpty()) throw new TokenNotFoundException("Nie odnaleziono tokenu.");

        PasswordRecoveryToken token = optionalToken.get();
        if (!token.isValid() || token.getExpiryDate().getTime() <= System.currentTimeMillis()) throw new TokenExpiredException("Wskazany token utracił swoją ważność.");

        token.setValid(false);
        User user = token.getUser();
        user.setVerified(true);
        passwordRecoveryTokenRepository.save(token);
        userRepository.save(user);
    }




    public void resendVerificationEmail(String email) throws InvalidEmailFormatException, UserNotFoundException, TokenExpiredException, LowDelayException {
        if (!UserDto.isEmailValid(email))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("Nie odnaleziono konta powiązanego z adresem %s", email));

        // Check is account isn't already verified
        if (user.get().isVerified()) throw new TokenExpiredException("Konto zostało już aktywowane.");


        // Check if user has recently sent request for resending email
        Optional<PasswordRecoveryToken> token = passwordRecoveryTokenRepository.findByUserAndValid(user.get(), true);
        if (token.isPresent()) {
            Date now = new Date(System.currentTimeMillis());
            long duration = (now.getTime() - token.get().getCreationDate().getTime()) / 1000;
            // If delay is smaller than 120 seconds
            if (duration < 120) throw new LowDelayException(String.format("Odczekaj %d sekund!", 120 - duration));
        }


        // Set already awaiting verification tokens invalid
        disableOldTokens(user.get());

        // Create new verification token
        createTokenAndSendEmail(user.get());
    }




    private void disableOldTokens(User user) {
        List<PasswordRecoveryToken> tokens = passwordRecoveryTokenRepository.findAllByUser(user);
        if (tokens.size() > 0) {
            for(PasswordRecoveryToken token : tokens)
                token.setValid(false);
            passwordRecoveryTokenRepository.saveAll(tokens);
        }
    }



    public long getLastValidTokenCooldown(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) return 0;

        // Check if user has recently sent request for resending email
        Optional<PasswordRecoveryToken> token = passwordRecoveryTokenRepository.findByUserAndValid(user.get(), true);
        if (token.isPresent()) {
            Date now = new Date(System.currentTimeMillis());
            long duration = (now.getTime() - token.get().getCreationDate().getTime()) / 1000;
            // If delay is smaller than 120 seconds
            if (duration > 120) return 0;

            return 120 - duration;
        }
        return 0;
    }



}
