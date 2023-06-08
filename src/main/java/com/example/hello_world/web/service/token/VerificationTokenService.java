package com.example.hello_world.web.service.token;

import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.VerificationToken;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.token.VerificationTokenRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.EmailService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

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


    public void verifyUserEmail(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException {
        // Check if token UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(tokenUuid)) throw new InvalidUUIDFormatException("Nieprawidłowy token.");

        //Check if token exists
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(UUID.fromString(tokenUuid));
        if (verificationToken.isEmpty()) throw new TokenNotFoundException("Nie odnaleziono tokenu.");

        VerificationToken token = verificationToken.get();
        if (!token.isValid() || token.getExpiryDate().getTime() <= System.currentTimeMillis()) throw new TokenExpiredException("Wskazany token utracił swoją ważność.");

        token.setValid(false);
        User user = token.getUser();
        user.setVerified(true);
        tokenRepository.save(token);
        userRepository.save(user);
    }


    @Override
    public void resendEmail(String email) throws InvalidEmailFormatException, UserNotFoundException, TokenExpiredException, LowDelayException {
        if (!UserDto.isEmailValid(email))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("Nie odnaleziono konta powiązanego z adresem %s", email));

        // Check is account isn't already verified
        if (user.get().isVerified()) throw new TokenExpiredException("Konto zostało już aktywowane.");

        super.resendEmail(user.get());
    }



}
