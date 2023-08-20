package com.example.vertonowsky.user;

import com.example.vertonowsky.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/user/courses")
    public ModelAndView getUserData(@RequestParam("userDataType") String userDataType, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!userService.isLoggedIn(auth)) return new ModelAndView("redirect:/");

        // Get the user
        User user = userService.get(auth, UserQueryType.COURSES_OWNED);
        if (user == null) return null;

        model.addAttribute("ownedCourses", user.getCoursesOwned());
        return new ModelAndView("profil :: course-list");

    }

}
