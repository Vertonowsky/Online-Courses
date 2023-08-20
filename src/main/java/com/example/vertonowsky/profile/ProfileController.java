package com.example.vertonowsky.profile;

import com.example.vertonowsky.course.CourseDto;
import com.example.vertonowsky.course.CourseSerializer;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserInfoDto;
import com.example.vertonowsky.user.UserQueryType;
import com.example.vertonowsky.user.UserSerializer;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.example.vertonowsky.course.CourseSerializer.Task.BASE;
import static com.example.vertonowsky.course.CourseSerializer.Task.PAYMENTS;
import static com.example.vertonowsky.user.UserSerializer.Task.AVATAR;

@Controller
public class ProfileController {

    @Value("${server.url}")
    private String serverUrl;
    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    /**
     *
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML page represting user's profile page
     */
    @GetMapping("/profil")
    public ModelAndView profil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(auth, UserQueryType.FINISHED_TOPICS_AND_COURSES_OWNED_AND_PAYMENT_HISTORIES);
        if (user == null) return new ModelAndView("redirect:/");

        UserInfoDto userDto = UserSerializer.serialize(user, AVATAR, UserSerializer.Task.PAYMENTS);
        List<CourseOwned> courseOwnedList = userService.sortCoursesOwned(user);
        if (courseOwnedList != null) {
            // Get every bought course progress
            List<CourseDto> courseDtos = courseOwnedList.stream().map(courseOwned -> CourseSerializer.serialize(courseOwned, BASE, PAYMENTS)).toList();
            profileService.calculateCourseCompletion(courseDtos, user);
            userDto.setCourses(courseDtos);
        }

        // Get recent progress based on topic completion
        model.addAttribute("recentProgress", profileService.calculateRecentProgress(user));
        model.addAttribute("serverUrl", serverUrl);
        model.addAttribute("user", userDto);
        return new ModelAndView("profil");
    }
}
