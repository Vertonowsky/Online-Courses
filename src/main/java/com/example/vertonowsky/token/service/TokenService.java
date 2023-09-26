package com.example.vertonowsky.token.service;

import com.example.vertonowsky.Collections;
import com.example.vertonowsky.ErrorCode;
import com.example.vertonowsky.Regex;
import com.example.vertonowsky.email.EmailService;
import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.security.RegistrationMethod;
import com.example.vertonowsky.token.model.Token;
import com.example.vertonowsky.token.repository.TokenRepository;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserDto;
import com.example.vertonowsky.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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


    public abstract void verifyToken(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException, UserVerificationException;

    protected abstract void createTokenAndSendEmail(User user);


    protected void disableOldTokens(User user, Token... avoidTokens) {
        List<T> tokens = tokenRepository.findByUserAndValid(user, true);
        if (Collections.isNullOrEmpty(tokens)) return;

        tokens = tokens.stream().filter(token -> Arrays.stream(avoidTokens).noneMatch(avoidToken -> avoidToken != null && avoidToken.equals(token))).toList();

        for (T t : tokens)
            t.setValid(false);


        tokenRepository.saveAll(tokens);
    }


    protected long getLastTokenGenerationCooldown(User user) {
        // Check if user has recently sent request for resending email
        List<T> tokens = tokenRepository.findByUserAndValid(user, true);
        if (Collections.isNullOrEmpty(tokens)) return 0;

        Optional<T> token = tokens.stream().findFirst();
        if (token.isEmpty()) return 0;

        OffsetDateTime now = OffsetDateTime.now();
        Duration duration = Duration.between(token.get().getCreationDate(), now);
        return duration.getSeconds();
    }


    protected void verifyCooldown(User user) throws LowDelayException {
        long seconds = getLastTokenGenerationCooldown(user);
        // If delay is smaller than 120 seconds
        if (seconds < 120) throw new LowDelayException(String.format("Odczekaj %d sekund!", 120 - seconds));
    }


    public void resendEmail(String email) throws UserNotFoundException, LowDelayException, InvalidEmailFormatException, UserVerificationException {
        resendEmail(findUser(email));
    }


    protected User findUser(String email) throws InvalidEmailFormatException, UserNotFoundException, UserVerificationException {
        if (!UserDto.isEmailValid(email))
            throw new InvalidEmailFormatException("Nieprawidłowy format adresu e-mail.");

        // Check if user exists
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("Nie odnaleziono konta powiązanego z adresem %s", email));

        if (!RegistrationMethod.DEFAULT.equals(user.get().getRegistrationMethod()))
            throw new UserVerificationException(ErrorCode.REGISTRATION_METHOD_INVALID.getMessage());

        return user.get();
    }


    protected T findToken(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException {
        // Check if token UUID matches the pattern
        if (!Regex.UUID_PATTERN.matches(tokenUuid)) throw new InvalidUUIDFormatException(ErrorCode.TOKEN_INVALID.getMessage());

        //Check if token exists
        Optional<T> optionalToken = tokenRepository.findByToken(UUID.fromString(tokenUuid));
        if (optionalToken.isEmpty()) throw new TokenNotFoundException(ErrorCode.TOKEN_NOT_FOUND.getMessage());

        T token = optionalToken.get();
        if (!token.isValid() || OffsetDateTime.now().isAfter(token.getExpiryDate())) throw new TokenExpiredException(ErrorCode.TOKEN_EXPIRED.getMessage());

        return token;
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

        long seconds = getLastTokenGenerationCooldown(user.get());

        // If delay is smaller than 120 seconds
        if (seconds > 120) return 0;

        return 120 - seconds;
    }

}
