package com.example.hello_world.web.controller;


import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ProfileController {


    @Autowired
    UserRepository userRepository;


    @GetMapping("/profil")
    public ModelAndView profil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails currentUserName = null;
        if (auth.getPrincipal() instanceof MyUserDetails) {
            currentUserName = (MyUserDetails) auth.getPrincipal();

            Optional<User> optionalUser = userRepository.findByEmail(currentUserName.getUsername());
            if (optionalUser.isEmpty()) return new ModelAndView("redirect:/");

            User user = optionalUser.get();

            model.addAttribute("ownedCourses", user.getCoursesOwned());
            model.addAttribute("paymentHistory", user.getPaymentHistories());
            return new ModelAndView("profil");

        }
        return new ModelAndView("redirect:/");
    }


}
