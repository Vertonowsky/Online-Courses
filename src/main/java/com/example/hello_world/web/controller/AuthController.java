package com.example.hello_world.web.controller;


import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.validation.UserAlreadyExistsException;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RestController
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    private static String authorizationRequestBaseUri = "oauth2/authorization";

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;


    @GetMapping("/logowanie")
    public ModelAndView showLoginForm(@RequestParam(value = "registered", required = false) boolean registered) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            ModelAndView mav = new ModelAndView( "logowanie");
            mav.addObject("googleUrl", getGoogleLoginUrl());
            mav.addObject("registered", registered);
            return mav;
        }

        return new ModelAndView( "redirect:/");
    }



    @PostMapping("/logowanie")
    public ModelAndView showLoginform(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            ModelAndView mav = new ModelAndView( "logowanie");
            mav.addObject("googleUrl", getGoogleLoginUrl());
            mav.addObject("registered", false);
            return mav;
        }

        return new ModelAndView( "redirect:/");
    }


    @GetMapping("/rejestracja")
    public ModelAndView RegisterView(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new ModelAndView( "rejestracja");
        }

        return new ModelAndView( "redirect:/");
    }




    @PostMapping("/auth/register")
    public HashMap<String, Object> registerUserAccount (@ModelAttribute("user") UserDto userDto) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", "register");
        map.put("success", false);

        if (!userDto.isEmailValid()) {
            map.put("message", "Nieprawidłowy adres e-mail.");
            return map;
        }
        if (!userDto.isPasswordValid()) {
            map.put("message", "Hasło nie spełnia wymagań bezpieczeństwa.");
            return map;
        }
        if (!userDto.arePasswordsEqual()) {
            map.put("message", "Podane hasła nie są identyczne.");
            return map;
        }

        if (!userDto.areTermsChecked()) {
            map.put("message", "Wymagana jest akceptacja regulaminu.");
            return map;
        }


        try {
            userService.registerNewUserAccount(userDto);

        } catch (UserAlreadyExistsException uaeEx) {
            map.put("message", "Konto o podanym adresie email już istnieje.");
            return map;
        }


        map.replace("success", false, true);
        map.put("message", "Pomyślnie utworzono konto!");
        return map;
    }


    @GetMapping("/oauth_login")
    public ModelAndView getLoginPage(Model model) {
        model.addAttribute("googleUrl", getGoogleLoginUrl());
        return new ModelAndView("oauth_login");
    }


    @GetMapping("/list-users")
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }


    private String getGoogleLoginUrl() {
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
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
