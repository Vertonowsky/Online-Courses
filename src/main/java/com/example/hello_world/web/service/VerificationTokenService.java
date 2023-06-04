package com.example.hello_world.web.service;

import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.VerificationToken;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.VerificationTokenRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VerificationTokenService {

    @Value("${verification.email.url}")
    private String verificationEmailUrl;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;




    public VerificationTokenService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }



    public void createTokenAndSendEmail(User user) {
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        verificationTokenRepository.save(token);


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




    public void resendVerificationEmail(String email) throws InvalidEmailFormatException, UserNotFoundException, TokenExpiredException, LowDelayException {
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



    public long getLastValidTokenCooldown(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) return 0;

        // Check if user has recently sent request for resending email
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUserAndValid(user.get(), true);
        if (verificationToken.isPresent()) {
            Date now = new Date(System.currentTimeMillis());
            long duration = (now.getTime() - verificationToken.get().getCreationDate().getTime()) / 1000;
            // If delay is smaller than 120 seconds
            if (duration > 120) return 0;

            return 120 - duration;
        }
        return 0;
    }



}
