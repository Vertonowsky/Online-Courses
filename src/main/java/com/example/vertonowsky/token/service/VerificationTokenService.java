package com.example.vertonowsky.token.service;

import com.example.vertonowsky.email.EmailService;
import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.token.model.VerificationToken;
import com.example.vertonowsky.token.repository.VerificationTokenRepository;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class VerificationTokenService extends TokenService<VerificationToken, VerificationTokenRepository> {


    public VerificationTokenService(EmailService emailService, UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        super(emailService, userRepository, tokenRepository);
    }


    @Override
    public void createTokenAndSendEmail(User user) {
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        tokenRepository.save(token);


        // Send verification email
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("to", user.getEmail());
        variables.put("url", websiteUrl + "/auth/verify?token=" + token.getToken().toString());

        emailService.sendHtmlEmail(user.getEmail(), "Potwierdź swoją rejestrację - Kursowo.pl", variables, "templates/template-email-verification.html");
    }





    @Override
    public void verifyToken(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException {
        VerificationToken token = super.findToken(tokenUuid);

        token.setValid(false);
        User user = token.getUser();
        user.setVerified(true);
        tokenRepository.save(token);
        userRepository.save(user);
    }




    @Override
    public void resendEmail(String email) throws InvalidEmailFormatException, UserNotFoundException, LowDelayException, UserVerificationException {
        User user = findUser(email);

        // Check is account isn't already verified
        if (user.isVerified()) throw new UserVerificationException("Konto zostało już aktywowane.");

        super.resendEmail(user);
    }



}
