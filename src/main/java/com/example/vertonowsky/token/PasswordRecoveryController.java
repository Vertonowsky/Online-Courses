package com.example.vertonowsky.token;

import com.example.vertonowsky.Collections;
import com.example.vertonowsky.ErrorCode;
import com.example.vertonowsky.Regex;
import com.example.vertonowsky.ResponseDto;
import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.security.RegistrationMethod;
import com.example.vertonowsky.token.model.PasswordRecoveryToken;
import com.example.vertonowsky.token.service.PasswordRecoveryTokenService;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserQueryType;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@RestController
public class PasswordRecoveryController {

    private final UserService userService;

    private final PasswordRecoveryTokenService passwordRecoveryTokenService;

    public PasswordRecoveryController(UserService userService, PasswordRecoveryTokenService passwordRecoveryTokenService) {
        this.userService = userService;
        this.passwordRecoveryTokenService = passwordRecoveryTokenService;
    }

    @GetMapping("/przywracanie-hasla")
    public ModelAndView showPasswordRecoveryForm(Model model) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        model.addAttribute("progress", RecoverPasswordStage.FRESH);
        return new ModelAndView( "przywracanie-hasla");
    }



    @PostMapping("/auth/password/recovery/start")
    public Object startRecoveryProcess(@RequestParam(value = "email") String email, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        try {

            passwordRecoveryTokenService.initializeToken(email);

        } catch (InvalidEmailFormatException | UserNotFoundException | LowDelayException | TokenExpiredException | UserVerificationException e) {
            model.addAttribute("error", "Błąd: " + e.getMessage());
            model.addAttribute("dataEmail", email);
            model.addAttribute("progress", RecoverPasswordStage.FRESH);
            return new ModelAndView("przywracanie-hasla");
        }

        RedirectView rv = new RedirectView("/weryfikacja", true);
        rv.addStaticAttribute("email", email);
        rv.addStaticAttribute("verificationType", VerificationType.PASSWORD_RECOVER_NEW.getIndex());
        return new ModelAndView(rv);
    }


    @GetMapping("/auth/password/recovery/recover")
    public Object verifyPasswordToken(Model model, @RequestParam(value = "token") String tokenUuid) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(auth, UserQueryType.BASE);
        if (userService.isLoggedIn(user)) return new ModelAndView( "redirect:/");

        try {

            passwordRecoveryTokenService.verifyToken(tokenUuid);
            model.addAttribute("progress", RecoverPasswordStage.NEW_PASSWORD_CREATION);

            PasswordRecoverDto passwordDto = new PasswordRecoverDto();
            model.addAttribute("passwordDto", passwordDto);

        } catch (Exception e) {
            model.addAttribute("criticalError", e.getMessage());
        }

        return new ModelAndView("przywracanie-hasla");
    }

    @PostMapping("/auth/password/recovery/resend/email")
    public ResponseDto resendPasswordRecoveryEmail(@RequestParam(value = "email") String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.isLoggedIn(auth)) {
            ResponseDto dto = new ResponseDto(false, "Błąd: Strona niedostępna dla zalogowanych użytkowników.");
            dto.setTokenCooldown(120L);
            return dto;
        }

        try {

            passwordRecoveryTokenService.resendEmail(email);
            ResponseDto dto = new ResponseDto(true, "Wysłano nowy email weryfikacyjny. Sprawdź pocztę e-mail.");
            dto.setTokenCooldown(120L);
            return dto;

        } catch (Exception e) {
            ResponseDto dto = new ResponseDto(false, e.getMessage());
            dto.setTokenCooldown(passwordRecoveryTokenService.getLastValidTokenCooldown(email));
            return dto;
        }
    }



    @PostMapping("/auth/password/recovery/confirm")
    public ModelAndView confirmPasswordChange(Model model, @ModelAttribute("passwordDto") PasswordRecoverDto passwordRecoverDto) {
        try {

            passwordRecoveryTokenService.changeUserPassword(passwordRecoverDto);
            model.addAttribute("progress", RecoverPasswordStage.PASSWORD_CHANGED);

        } catch (InvalidPasswordFormatException | PasswordsNotEqualException | InvalidUUIDFormatException |
                 TokenExpiredException | TokenNotFoundException | UserNotFoundException | UserVerificationException e) {
            model.addAttribute("criticalError", e.getMessage());
        }

        return new ModelAndView("przywracanie-hasla");
    }



    @PostMapping("/auth/password/change/authorized/start")
    public ResponseDto startAuthorizedPasswordChangeProcess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(auth, UserQueryType.BASE);
        if (user == null || !userService.isLoggedIn(user))
            return new ResponseDto(false, ErrorCode.LOGIN_REQUIRED.getMessage());

        if (!RegistrationMethod.DEFAULT.equals(user.getRegistrationMethod()))
            return new ResponseDto(false, ErrorCode.REGISTRATION_METHOD_INVALID.getMessage());

        PasswordRecoveryToken passwordRecoveryToken = passwordRecoveryTokenService.createAuthorizedToken(user);
        return new ResponseDto(true, passwordRecoveryToken.getToken().toString());
    }


    @GetMapping("/auth/password/change/authorized/proceed")
    public Object authorizedPasswordChange(@RequestParam(value = "token") String tokenUuid, Model model) {
        if (Collections.isNullOrEmpty(tokenUuid) || !Regex.UUID_PATTERN.matches(tokenUuid))
            return new ResponseDto(ErrorCode.TOKEN_INVALID.getMessage());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(auth, UserQueryType.BASE);
        if (user == null  || !userService.isLoggedIn(user))
            return new ResponseDto(ErrorCode.LOGIN_REQUIRED.getMessage());

        if (!RegistrationMethod.DEFAULT.equals(user.getRegistrationMethod()))
            return new ResponseDto(false, ErrorCode.REGISTRATION_METHOD_INVALID.getMessage());

        try {

            passwordRecoveryTokenService.updateRecoverPasswordStage(tokenUuid, RecoverPasswordStage.NEW_PASSWORD_CREATION_AUTHORIZED);
            model.addAttribute("progress", RecoverPasswordStage.NEW_PASSWORD_CREATION_AUTHORIZED);

            PasswordRecoverDto passwordDto = new PasswordRecoverDto();
            passwordDto.setToken(UUID.fromString(tokenUuid));
            model.addAttribute("passwordDto", passwordDto);

        } catch (Exception e) {
            model.addAttribute("criticalError", e.getMessage());
        }

        return new ModelAndView("przywracanie-hasla");
    }

}
