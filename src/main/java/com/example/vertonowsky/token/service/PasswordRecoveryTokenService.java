package com.example.vertonowsky.token.service;

import com.example.vertonowsky.Collections;
import com.example.vertonowsky.ErrorCode;
import com.example.vertonowsky.email.EmailService;
import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.security.RegistrationMethod;
import com.example.vertonowsky.token.PasswordRecoverDto;
import com.example.vertonowsky.token.RecoverPasswordStage;
import com.example.vertonowsky.token.model.PasswordRecoveryToken;
import com.example.vertonowsky.token.repository.PasswordRecoveryTokenRepository;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserDto;
import com.example.vertonowsky.user.UserRepository;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordRecoveryTokenService extends TokenService<PasswordRecoveryToken, PasswordRecoveryTokenRepository> {

    private final UserService userService;

    public PasswordRecoveryTokenService(EmailService emailService, UserRepository userRepository, PasswordRecoveryTokenRepository tokenRepository, UserService userService) {
        super(emailService, userRepository, tokenRepository);
        this.userService = userService;
    }


    public void initializeToken(String email) throws InvalidEmailFormatException, UserNotFoundException, LowDelayException, TokenExpiredException, UserVerificationException {
        User user = findUser(email);

        // Check is account isn't already verified
        if (!user.isVerified()) throw new UserVerificationException("Konto nie jest aktywne.");

        super.resendEmail(user);
    }


    public PasswordRecoveryToken createAuthorizedToken(User user) {
        // Check if user has recently sent request for resending email
        PasswordRecoveryToken token = analyseCooldown(user);

        // Set already awaiting verification tokens invalid
        disableOldTokens(user, token);

        return token != null ? token : createToken(user, RecoverPasswordStage.FRESH_AUTHORIZED);
    }

    private PasswordRecoveryToken analyseCooldown(User user) {
        // Check if user has recently sent request for resending email
        List<PasswordRecoveryToken> tokens = tokenRepository.findByUserAndValid(user, true);
        if (Collections.isNullOrEmpty(tokens)) return null;

        Optional<PasswordRecoveryToken> token = tokens.stream().findFirst();
        if (token.isEmpty()) return null;

        OffsetDateTime now = OffsetDateTime.now();
        Duration duration = Duration.between(token.get().getCreationDate(), now);
        long seconds = duration.getSeconds();

        // If delay is smaller than 120 seconds
        if (seconds < 120) return token.get();

        return null;
    }

    public PasswordRecoveryToken createToken(User user, RecoverPasswordStage recoverPasswordStage) {
        PasswordRecoveryToken token = new PasswordRecoveryToken();
        token.setUser(user);
        token.setRecoverPasswordStage(recoverPasswordStage);
        tokenRepository.save(token);

        return token;
    }

    public void updateRecoverPasswordStage(String tokenUuid, RecoverPasswordStage stage) {
        Optional<PasswordRecoveryToken> passwordRecoveryToken = tokenRepository.findByToken(UUID.fromString(tokenUuid));
        if (passwordRecoveryToken.isEmpty()) return;

        PasswordRecoveryToken token = passwordRecoveryToken.get();
        token.setRecoverPasswordStage(stage);
        tokenRepository.save(token);
    }


    @Override
    protected void createTokenAndSendEmail(User user) {
        PasswordRecoveryToken token = new PasswordRecoveryToken();
        token.setUser(user);
        tokenRepository.save(token);


        // Send verification email
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("to", user.getEmail());
        variables.put("url", websiteUrl + "/auth/password/recovery/recover?token=" + token.getToken().toString());

        emailService.sendHtmlEmail( user.getEmail(), "Odzyskiwanie hasła - Kursowo.pl", variables, "templates/template-email-password-recover.html");
    }



    @Override
    public void verifyToken(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException, UserVerificationException {
        PasswordRecoveryToken token = super.findToken(tokenUuid);

        User user = token.getUser();
        if (user != null && !RegistrationMethod.DEFAULT.equals(user.getRegistrationMethod()))
            throw new UserVerificationException(ErrorCode.REGISTRATION_METHOD_INVALID.getMessage());

        token.setRecoverPasswordStage(RecoverPasswordStage.NEW_PASSWORD_CREATION);
        tokenRepository.save(token);
    }


    public void changeUserPassword(PasswordRecoverDto recoverPasswordDto) throws InvalidPasswordFormatException, PasswordsNotEqualException, InvalidUUIDFormatException, TokenExpiredException, TokenNotFoundException, UserNotFoundException, UserVerificationException {

        PasswordRecoveryToken token = findToken(recoverPasswordDto.getToken().toString());

        if (!UserDto.isPasswordValid(recoverPasswordDto.getPassword()))
            throw new InvalidPasswordFormatException("Hasło nie spełnia wymagań bezpieczeństwa.");

        if (!recoverPasswordDto.arePasswordsEqual())
            throw new PasswordsNotEqualException("Podane hasła nie są identyczne.");

        Optional<User> user = userService.getUserByPasswordRecoveryToken(recoverPasswordDto.getToken());
        if (user.isEmpty())
            throw new UserNotFoundException("Nie odnaleziono użytkownika.");

        if (!RegistrationMethod.DEFAULT.equals(user.get().getRegistrationMethod()))
            throw new UserVerificationException(ErrorCode.REGISTRATION_METHOD_INVALID.getMessage());

        // Check is account isn't already verified
        if (!user.get().isVerified()) throw new UserVerificationException("Konto nie jest aktywne.");

        userService.changePassword(user.get(), recoverPasswordDto.getPassword());
        token.setRecoverPasswordStage(RecoverPasswordStage.PASSWORD_CHANGED);
        token.setValid(false);
        tokenRepository.save(token);
    }

}
