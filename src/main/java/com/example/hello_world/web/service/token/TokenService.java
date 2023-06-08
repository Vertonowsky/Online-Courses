package com.example.hello_world.web.service.token;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.Token;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.token.TokenRepository;
import com.example.hello_world.validation.InvalidEmailFormatException;
import com.example.hello_world.validation.LowDelayException;
import com.example.hello_world.validation.TokenExpiredException;
import com.example.hello_world.validation.UserNotFoundException;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.EmailService;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public abstract class TokenService<T extends Token, S extends TokenRepository<T>> {


    @Value("${verification.email.url}")
    protected String websiteUrl;

    protected final S tokenRepository;
    protected final UserRepository userRepository;
    protected final EmailService emailService;

    public TokenService(EmailService emailService, UserRepository userRepository, S tokenRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }




    protected abstract void createTokenAndSendEmail(User user);


    protected void disableOldTokens(User user) {
        List<T> tokens = tokenRepository.findAllByUser(user);
        if (tokens.size() > 0) {
            for(T t : tokens)
                t.setValid(false);
            tokenRepository.saveAll(tokens);
        }
    }


    protected void verifyCooldown(User user) throws LowDelayException {
        // Check if user has recently sent request for resending email
        Optional<T> token = tokenRepository.findByUserAndValid(user, true);
        if (token.isPresent()) {
            Date now = new Date(System.currentTimeMillis());
            long duration = (now.getTime() - token.get().getCreationDate().getTime()) / 1000;
            // If delay is smaller than 120 seconds
            if (duration < 120) throw new LowDelayException(String.format("Odczekaj %d sekund!", 120 - duration));
        }
    }



    public void resendEmail(String email) throws InvalidEmailFormatException, UserNotFoundException, TokenExpiredException, LowDelayException {
        if (!UserDto.isEmailValid(email))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("Nie odnaleziono konta powiązanego z adresem %s", email));

        resendEmail(user.get());
    }


    protected void resendEmail(User user) throws LowDelayException {
        // Check if user has recently sent request for resending email
        verifyCooldown(user);

        // Set already awaiting verification tokens invalid
        disableOldTokens(user);

        // Create new verification token
        createTokenAndSendEmail(user);
    }




    public long getLastValidTokenCooldown(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) return 0;

        // Check if user has recently sent request for resending email
        Optional<T> token = tokenRepository.findByUserAndValid(user.get(), true);
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
