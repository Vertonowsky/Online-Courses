package com.example.vertonowsky.token.service;

import com.example.vertonowsky.email.EmailService;
import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.token.PasswordRecoverDto;
import com.example.vertonowsky.token.RecoverPasswordStage;
import com.example.vertonowsky.token.model.PasswordRecoveryToken;
import com.example.vertonowsky.token.repository.PasswordRecoveryTokenRepository;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserDto;
import com.example.vertonowsky.user.UserRepository;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

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



    @Override
    public void verifyToken(String tokenUuid) throws InvalidUUIDFormatException, TokenNotFoundException, TokenExpiredException {
        PasswordRecoveryToken token = super.findToken(tokenUuid);

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

        // Check is account isn't already verified
        if (!user.get().isVerified()) throw new UserVerificationException("Konto nie jest aktywne.");

        userService.changePassword(user.get(), recoverPasswordDto.getPassword());
        token.setRecoverPasswordStage(RecoverPasswordStage.PASSWORD_CHANGED);
        token.setValid(false);
        tokenRepository.save(token);
    }

}
