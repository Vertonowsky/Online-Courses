package com.example.hello_world.web.service.token;

import com.example.hello_world.RecoverPasswordStage;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.PasswordRecoveryToken;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.token.PasswordRecoveryTokenRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.PasswordRecoverDto;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.EmailService;
import com.example.hello_world.web.service.UserService;
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


    public void initializeToken(String email) throws InvalidEmailFormatException, UserNotFoundException, LowDelayException, TokenExpiredException {
        super.resendEmail(email);
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


    public void changeUserPassword(PasswordRecoverDto recoverPasswordDto) throws InvalidPasswordFormatException, PasswordsNotEqualException, InvalidUUIDFormatException, TokenExpiredException, TokenNotFoundException, UserNotFoundException {

        PasswordRecoveryToken token = findToken(recoverPasswordDto.getToken().toString());

        if (!UserDto.isPasswordValid(recoverPasswordDto.getPassword()))
            throw new InvalidPasswordFormatException("Hasło nie spełnia wymagań bezpieczeństwa.");

        if (!recoverPasswordDto.arePasswordsEqual())
            throw new PasswordsNotEqualException("Podane hasła nie są identyczne.");

        Optional<User> user = userService.getUserByPasswordRecoveryToken(recoverPasswordDto.getToken());
        if (user.isEmpty())
            throw new UserNotFoundException("Nie odnaleziono użytkownika.");

        userService.changePassword(user.get(), recoverPasswordDto.getPassword());
        token.setRecoverPasswordStage(RecoverPasswordStage.PASSWORD_CHANGED);
        token.setValid(false);
        tokenRepository.save(token);
    }


}
