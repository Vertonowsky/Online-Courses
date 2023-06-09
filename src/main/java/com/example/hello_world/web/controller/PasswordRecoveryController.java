package com.example.hello_world.web.controller;

import com.example.hello_world.RecoverPasswordStage;
import com.example.hello_world.VerificationType;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.dto.PasswordRecoverDto;
import com.example.hello_world.web.service.token.PasswordRecoveryTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;

@RestController
public class PasswordRecoveryController {


    private final PasswordRecoveryTokenService passwordRecoveryTokenService;

    public PasswordRecoveryController(PasswordRecoveryTokenService passwordRecoveryTokenService) {
        this.passwordRecoveryTokenService = passwordRecoveryTokenService;
    }


    @GetMapping("/przywracanie-hasla")
    public ModelAndView showPasswordRecoveryForm(Model model) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        model.addAttribute("progress", RecoverPasswordStage.FRESH);
        return new ModelAndView( "przywracanie-hasla");
    }



    @PostMapping("/auth/startPasswordRecoveryProcess")
    public ModelAndView startRecoveryProcess(@RequestParam(value = "email") String email, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

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


    @GetMapping("/auth/recoverPassword")
    public ModelAndView verifyPasswordToken(Model model, @RequestParam(value = "token") String tokenUuid) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

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




    @PostMapping("/auth/resendPasswordRecoveryEmail")
    public HashMap<String, Object> resendPasswordRecoveryEmail(@RequestParam(value = "email") String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "Błąd: Strona niedostępna dla zalogowanych użytkowników.");
            map.put("tokenCooldown", 120);
            return map;
        }
        HashMap<String, Object> map = new HashMap<>();
        try {

            passwordRecoveryTokenService.resendEmail(email);
            map.put("success", true);
            map.put("message", "Wysłano nowy email weryfikacyjny. Sprawdź pocztę e-mail.");
            map.put("tokenCooldown", 120);

        } catch (Exception e) {
            map.put("success", false);
            map.put("message", e.getMessage());
            map.put("tokenCooldown", passwordRecoveryTokenService.getLastValidTokenCooldown(email));
        }

        return map;
    }



    @PostMapping("/auth/confirmPasswordChange")
    public ModelAndView confirmPasswordChange(Model model, @ModelAttribute("passwordDto") PasswordRecoverDto passwordRecoverDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        try {

            passwordRecoveryTokenService.changeUserPassword(passwordRecoverDto);
            model.addAttribute("progress", RecoverPasswordStage.PASSWORD_CHANGED);

        } catch (InvalidPasswordFormatException | PasswordsNotEqualException | InvalidUUIDFormatException |
                 TokenExpiredException | TokenNotFoundException | UserNotFoundException | UserVerificationException e) {

            model.addAttribute("error", e.getMessage());

        }


        return new ModelAndView("przywracanie-hasla");
    }


}
