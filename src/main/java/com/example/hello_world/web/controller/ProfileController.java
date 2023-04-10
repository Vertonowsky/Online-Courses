package com.example.hello_world.web.controller;


import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.web.dto.CourseOwnedDto;
import com.example.hello_world.web.service.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Controller
public class ProfileController {


    private final UserRepository userRepository;
    private final ProfileService profileService;

    public ProfileController(UserRepository userRepository, ProfileService profileService) {
        this.userRepository = userRepository;
        this.profileService = profileService;
    }





    /**
     *
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML page represting user's profile page
     */
    @GetMapping("/profil")
    public ModelAndView profil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!User.isLoggedIn(auth)) return new ModelAndView("redirect:/");

        // Get the user
        Optional<User> optionalUser = userRepository.findByEmail(User.getEmail(auth));
        if (optionalUser.isEmpty()) return new ModelAndView("redirect:/");

        User user = optionalUser.get();


        // Get every bought course progress
        ArrayList<CourseOwnedDto> courseOwnedDtos = profileService.calculateCourseCompletion(user);

        // Get recent progress based on topic completion
        for (Map.Entry<String, Integer> set : profileService.calculateRecentProgress(user).entrySet())
            model.addAttribute(set.getKey(), set.getValue());


        model.addAttribute("ownedCourses", courseOwnedDtos);
        model.addAttribute("paymentHistory", user.getPaymentHistories());
        return new ModelAndView("profil");
    }
}
