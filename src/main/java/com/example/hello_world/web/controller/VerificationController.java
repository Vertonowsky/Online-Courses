package com.example.hello_world.web.controller;


import com.example.hello_world.Regex;
import com.example.hello_world.VerificationType;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.web.service.token.VerificationTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
@RestController
public class VerificationController {


    private final VerificationTokenService verificationTokenService;

    public VerificationController(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }



    @GetMapping("/auth/verify")
    public ModelAndView verifyUserAccount(Model model, @RequestParam(value = "token") String tokenUuid) {
        try {

            verificationTokenService.verifyToken(tokenUuid);
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

            verificationTokenService.resendEmail(email);
            map.put("success", true);
            map.put("message", "Wysłano nowy email weryfikacyjny. Sprawdź pocztę e-mail.");
            map.put("tokenCooldown", 120);

        } catch (Exception e) {
            map.put("success", false);
            map.put("message", e.getMessage());
            map.put("tokenCooldown", verificationTokenService.getLastValidTokenCooldown(email));
        }

        return map;
    }



    @GetMapping("/weryfikacja")
    public ModelAndView showVerifyForm(@RequestParam(value = "email") String email, @RequestParam(value = "verificationType") Integer verificationType, Model model) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (User.isLoggedIn(auth) || !Regex.EMAIL_PATTERN.matches(email)) return new ModelAndView( "redirect:/");
        if (!VerificationType.isValidIndex(verificationType)) return new ModelAndView( "redirect:/");

        model.addAttribute("email", email);
        model.addAttribute("resendUrl", "/auth/resendVerificationEmail");
        model.addAttribute("verificationType", verificationType);
        return new ModelAndView( "weryfikacja");
    }


}
