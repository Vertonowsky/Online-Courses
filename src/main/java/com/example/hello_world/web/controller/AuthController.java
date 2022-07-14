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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
@RestController
public class AuthController {

    @Autowired
    private IUserService userService;


    @GetMapping("/logowanie")
    public ModelAndView showLoginForm(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new ModelAndView( "logowanie");
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
}
