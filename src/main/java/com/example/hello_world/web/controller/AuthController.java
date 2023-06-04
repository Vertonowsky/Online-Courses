package com.example.hello_world.web.controller;


import com.example.hello_world.RecoverPasswordStage;
import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.PasswordRecoveryTokenService;
import com.example.hello_world.web.service.UserService;
import com.example.hello_world.web.service.VerificationTokenService;
import org.springframework.core.ResolvableType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.HashMap;

@Controller
@RestController
public class AuthController {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordRecoveryTokenService passwordRecoveryTokenService;
    private final ClientRegistrationRepository clientRegistrationRepository;


    public AuthController(UserService userService, VerificationTokenService verificationTokenService, PasswordRecoveryTokenService passwordRecoveryTokenService, ClientRegistrationRepository clientRegistrationRepository) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.passwordRecoveryTokenService = passwordRecoveryTokenService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }



    @GetMapping("/logowanie")
    public ModelAndView showLoginForm(@RequestParam(value = "registered", required = false) boolean registered) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        ModelAndView mav = new ModelAndView( "logowanie");
        mav.addObject("googleUrl", getGoogleLoginUrl());
        mav.addObject("registered", registered);
        return mav;
    }



    @PostMapping("/logowanie")
    public ModelAndView showLoginForm() {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        ModelAndView mav = new ModelAndView( "logowanie");
        mav.addObject("googleUrl", getGoogleLoginUrl());
        mav.addObject("registered", false);
        return mav;
    }


    @GetMapping("/rejestracja")
    public ModelAndView showRegisterForm(Model model) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return new ModelAndView( "rejestracja");
    }


    @PostMapping("/rejestracja")
    public ModelAndView registerUserAccount(Model model, @ModelAttribute("user") UserDto userDto) {
        try {

            userService.registerNewUserAccount(userDto);
            RedirectView rv = new RedirectView("weryfikacja", true);
            rv.addStaticAttribute("email", userDto.getEmail());
            return new ModelAndView(rv);

        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dataEmail", userDto.getEmail());
            model.addAttribute("dataPassword", userDto.getPassword());
            model.addAttribute("dataPasswordRepeat", userDto.getPasswordRepeat());
            model.addAttribute("dataTerms", userDto.areTermsChecked());
            return new ModelAndView("rejestracja");
        }
    }



    @GetMapping("/auth/verify")
    public ModelAndView verifyUserAccount(Model model, @RequestParam(value = "token") String tokenUuid) {
        try {

            verificationTokenService.verifyUserEmail(tokenUuid);
            model.addAttribute("verified", true);

        } catch (Exception e) {
            model.addAttribute("verified", false);
            model.addAttribute("verificationMessage", e.getMessage());
        }

        return new ModelAndView("logowanie");
    }



    @PostMapping("/auth/resendVerificationEmail")
    public HashMap<String, Object> resendEmail(@RequestParam(value = "email") String email) {
        HashMap<String, Object> map = new HashMap<>();
        try {

            verificationTokenService.resendVerificationEmail(email);
            map.put("success", true);
            map.put("message", "Wysłano nowy token weryfikacyjny. Sprawdź pocztę e-mail.");
            map.put("tokenCooldown", 120);

        } catch (Exception e) {
            map.put("success", false);
            map.put("message", e.getMessage());
            map.put("tokenCooldown", verificationTokenService.getLastValidTokenCooldown(email));
        }

        return map;
    }



    @GetMapping("/weryfikacja")
    public ModelAndView showVerifyForm(@RequestParam(value = "email") String email, @RequestParam(value = "loginAttempt", required = false) boolean loginAttempt, Model model) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth) || !Regex.EMAIL_PATTERN.matches(email)) return new ModelAndView( "redirect:/");

        model.addAttribute("email", email);
        model.addAttribute("loginAttempt", loginAttempt);
        return new ModelAndView( "weryfikacja");
    }


    @GetMapping("/przywracanie-hasla")
    public ModelAndView showPasswordRecoveryForm(Model model) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        return new ModelAndView( "przywracanie-hasla");
    }



    @PostMapping("/auth/recoverPassword")
    public ModelAndView startRecoveryProcess(@RequestParam(value = "email") String email, Model model) {
        try {

            passwordRecoveryTokenService.createPasswordRecoveryToken(email);
            model.addAttribute("progress", RecoverPasswordStage.NEW_PASSWORD);

        } catch (Exception e) {
            model.addAttribute("error", "Błąd: " + e.getMessage());
            model.addAttribute("dataEmail", email);
        }

        return new ModelAndView("przywracanie-hasla");
    }


    /**
     * Get google login url from Spring Security configuration
     *
     * @return google oAuth2 login url
     */
    private String getGoogleLoginUrl() {
        String authorizationRequestBaseUri = "oauth2/authorization";
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0]))
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;

        if (clientRegistrations != null) {
            for (ClientRegistration registration : clientRegistrations) {
                if (registration.getClientName().equalsIgnoreCase("google"))
                    return authorizationRequestBaseUri + "/" + registration.getRegistrationId();
            }
        }

        return "/";


        /*Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0]))
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;

        if (clientRegistrations != null) {
            for (ClientRegistration registration : clientRegistrations)
                oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId());
        }

        return oauth2AuthenticationUrls;*/
    }

}
