package com.example.vertonowsky.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class UserController {


    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/user/courses")
    public ModelAndView getUserData(@RequestParam("userDataType") String userDataType, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!User.isLoggedIn(auth)) return new ModelAndView("redirect:/");

        // Get the user
        Optional<User> optionalUser = userRepository.findByEmail(User.getEmail(auth));
        if (optionalUser.isEmpty()) return new ModelAndView("redirect:/");

        User user = optionalUser.get();


        model.addAttribute("ownedCourses", user.getCoursesOwned());
        return new ModelAndView("profil :: course-list");

    }

}
