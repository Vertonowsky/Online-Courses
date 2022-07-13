package com.example.hello_world.web.controller;


import com.example.hello_world.validation.UserAlreadyExistsException;
import com.example.hello_world.web.dto.UserDto;
import com.example.hello_world.web.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthController {

    @Autowired
    private IUserService userService;

    private final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


    @GetMapping("/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "registration";
        }

        return "redirect:/";
    }


    @PostMapping("/registration")
    public ModelAndView registerUserAccount (@ModelAttribute("user") UserDto userDto, HttpServletRequest request, Errors errors) {
        ModelAndView mav = new ModelAndView("registration", "user", userDto);

        if (!userDto.isEmailValid()) {
            return mav.addObject("message", "Nieprawidłowy adres e-mail.");
        }
        if (!userDto.isPasswordValid()) {
            return mav.addObject("message", "Hasło nie spełnia wymagań bezpieczeństwa.");
        }
        if (!userDto.arePasswordsEqual()) {
            return mav.addObject("message", "Podane hasła nie są identyczne.");
        }


        try {
            userService.registerNewUserAccount(userDto);

        } catch (UserAlreadyExistsException uaeEx) {
            mav.addObject("message", "Konto o podanym adresie email już istnieje.");
            return mav;
        }

        return new ModelAndView("successRegister", "user", userDto);
    }



    @GetMapping("/logowanie")
    public String showLoginForm(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "logowanie";
        }

        return "redirect:/";
    }

}
