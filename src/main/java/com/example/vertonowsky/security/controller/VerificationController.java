package com.example.vertonowsky.security.controller;


import com.example.vertonowsky.Regex;
import com.example.vertonowsky.ResponseDto;
import com.example.vertonowsky.token.VerificationType;
import com.example.vertonowsky.token.service.VerificationTokenService;
import com.example.vertonowsky.user.UserQueryType;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RestController
public class VerificationController {


    private final VerificationTokenService verificationTokenService;

    private final UserService userService;

    public VerificationController(VerificationTokenService verificationTokenService, UserService userService) {
        this.verificationTokenService = verificationTokenService;
        this.userService = userService;
    }

    @GetMapping("/auth/account/verify")
    public ModelAndView verifyUserAccount(Model model, @RequestParam(value = "token") String tokenUuid) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.get(auth, UserQueryType.ALL) != null) return new ModelAndView( "redirect:/");

        try {

            verificationTokenService.verifyToken(tokenUuid);
            model.addAttribute("verified", true);

        } catch (Exception e) {
            model.addAttribute("verified", false);
            model.addAttribute("verificationMessage", e.getMessage());
        }

        return new ModelAndView("logowanie");
    }


    @PostMapping("/auth/account/resend/email")
    public ResponseDto resendEmail(@RequestParam(value = "email") String email) {
        try {

            verificationTokenService.resendEmail(email);
            ResponseDto dto = new ResponseDto(true, "Wysłano nowy email weryfikacyjny. Sprawdź pocztę e-mail.");
            dto.setTokenCooldown(120L);
            return dto;

        } catch (Exception e) {
            ResponseDto dto = new ResponseDto(false, e.getMessage());
            dto.setTokenCooldown(verificationTokenService.getLastValidTokenCooldown(email));
            return dto;
        }
    }


    @GetMapping("/weryfikacja")
    public ModelAndView showVerifyForm(@RequestParam(value = "email") String email, @RequestParam(value = "verificationType") Integer verificationType, Model model) {
        // Check if user is already logged in.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.isLoggedIn(auth) || !Regex.EMAIL_PATTERN.matches(email)) return new ModelAndView( "redirect:/");
        if (!VerificationType.isValidIndex(verificationType)) return new ModelAndView( "redirect:/");

        model.addAttribute("email", email);
        model.addAttribute("verificationType", verificationType);

        if (verificationType == VerificationType.EMAIL_VERIFICATION_NEW.getIndex() || verificationType == VerificationType.EMAIL_VERIFICATION_LOGIN_ATTEMPT.getIndex())
            model.addAttribute("resendUrl", "/auth/account/resend/email");
        else if (verificationType == VerificationType.PASSWORD_RECOVER_NEW.getIndex())
            model.addAttribute("resendUrl", "/auth/password/recovery/resend/email");
        return new ModelAndView( "weryfikacja");
    }

}
