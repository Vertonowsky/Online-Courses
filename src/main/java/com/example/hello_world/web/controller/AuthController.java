package com.example.hello_world.web.controller;


import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.persistence.repository.VerificationTokenRepository;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.EmailService;
import com.example.hello_world.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@Controller
@RestController
public class AuthController {

    private UserService userService;
    private EmailService emailService;
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;



    @Autowired
    public void setUserDependency(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEmailDependency(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setClientRegistrationDependency(ClientRegistrationRepository clientRegistrationRepository) {
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




    @PostMapping("/auth/register")
    public ModelAndView registerUserAccount(@ModelAttribute("user") UserDto userDto) {
        //HashMap<String, Object> map = new HashMap<>();
        //map.put("type", "register");

        try {

            userService.registerNewUserAccount(userDto);
            return new ModelAndView("weryfikacja");

        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            //map.put("message", e.getMessage());
            //map.put("success", false);
            return new ModelAndView("rejestracja");
        }

        //map.put("success", true);
        //map.put("message", "Na podany adres email został wysłany kod weryfikacyjny.");
        //return map;
    }


    @GetMapping("/weryfikacja")
    public ModelAndView showVerifyForm(Model model) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth)) return new ModelAndView( "redirect:/");

        model.addAttribute("email", "klocek@wp.pl");
        return new ModelAndView( "weryfikacja");
    }



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
