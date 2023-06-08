package com.example.hello_world.web.service.token;

import com.example.hello_world.RecoverPasswordStage;
import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.PasswordRecoveryToken;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.token.PasswordRecoveryTokenRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.service.EmailService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordRecoveryTokenService extends TokenService<PasswordRecoveryToken, PasswordRecoveryTokenRepository> {


    public PasswordRecoveryTokenService(EmailService emailService, UserRepository userRepository, PasswordRecoveryTokenRepository tokenRepository) {
        super(emailService, userRepository, tokenRepository);
    }


    @Override
    protected void createTokenAndSendEmail(User user) {
        PasswordRecoveryToken token = new PasswordRecoveryToken();
        token.setUser(user);
        tokenRepository.save(token);


        // Send verification email
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("to", user.getEmail());
        variables.put("url", websiteUrl + "/auth/recoverPassword?token=" + token.getToken().toString());

        emailService.sendHtmlEmail( user.getEmail(), "Odzyskiwanie hasła - Kursowo.pl", variables, "templates/template-email-password-recover.html");
    }




    public void createPasswordRecoveryToken(String email) throws InvalidEmailFormatException, UserNotFoundException, LowDelayException {
        if (!Regex.EMAIL_PATTERN.matches(email))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("Nie odnaleziono konta o adresie email %s", email));


        // TODO check if user isn't currently changing his password


        // Check if user has recently sent request for resending email
        verifyCooldown(user.get());


        // Set already awaiting password recovry tokens invalid
        disableOldTokens(user.get());

        // Create new verification token
        createTokenAndSendEmail(user.get());
    }




    public void verifyToken(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException {
        // Check if token UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(tokenUuid)) throw new InvalidUUIDFormatException("Nieprawidłowy token.");

        //Check if token exists
        Optional<PasswordRecoveryToken> optionalToken = tokenRepository.findByToken(UUID.fromString(tokenUuid));
        if (optionalToken.isEmpty()) throw new TokenNotFoundException("Nie odnaleziono tokenu.");

        PasswordRecoveryToken token = optionalToken.get();
        if (!token.isValid() || token.getExpiryDate().getTime() <= System.currentTimeMillis()) throw new TokenExpiredException("Wskazany token utracił swoją ważność.");

        token.setRecoverPasswordStage(RecoverPasswordStage.NEW_PASSWORD_CREATION);
        tokenRepository.save(token);
    }


}
