package com.example.hello_world.web.controller;


import com.example.hello_world.persistence.model.CourseOwned;
import com.example.hello_world.persistence.model.FinishedTopic;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.web.dto.CourseOwnedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller
public class ProfileController {


    @Autowired
    UserRepository userRepository;


    @GetMapping("/profil")
    public ModelAndView profil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!User.isLoggedIn(auth)) return new ModelAndView("redirect:/");


        Optional<User> optionalUser = userRepository.findByEmail(User.getEmail(auth));
        if (optionalUser.isEmpty()) return new ModelAndView("redirect:/");

        User user = optionalUser.get();
        Date now = new Date(System.currentTimeMillis());
        LocalDate localDate1 = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int lastDay = 0, lastWeek = 0, lastMonth = 0;


        // Calculate how many goals have been reached recently
        for (FinishedTopic ft : user.getFinishedTopics()) {
            long difference = now.getTime() - ft.getDate().getTime();  // Difference in milliseconds between two dates (always positive)
            LocalDate localDate2 = ft.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (localDate1.isEqual(localDate2))  // Check if it is the same day
                lastDay++;
            if (difference / (1000 * 60 * 60 * 24) <= 7)  // Difference in time converted to days
                lastWeek++;
            if (difference / (1000 * 60 * 60 * 24) <= 30)  // Difference in time converted to days
                lastMonth++;
        }

        // Make it be percentage
        int lastDayPercentage = Math.min(lastDay * 100, 100);
        int lastWeekPercentage = Math.min((int)((lastWeek / (double)7) * 100), 100);
        int lastMonthPercentage = Math.min((int)((lastMonth / (double)30) * 100), 100);



        ArrayList<CourseOwnedDto> courseOwnedDtos = new ArrayList<>();
        for (CourseOwned co : user.getCoursesOwned()) {
            CourseOwnedDto cod = new CourseOwnedDto(co.getCourse(), co.getExpiryDate());
            int mainId = cod.getCourse().getId();
            int topicCount = co.getCourse().getTopicCount();

            for (FinishedTopic ft : user.getFinishedTopics()) {
                if (ft.getTopic().getChapter().getCourse().getId().equals(mainId)) {
                    cod.setFinishedTopics(cod.getFinishedTopics() + 1);
                }
            }

            int percentage = (int)((cod.getFinishedTopics() / (double)topicCount) * 100);
            if (percentage > 100) percentage = 100;
            cod.setPercentage(percentage);


            courseOwnedDtos.add(cod);
        }


        model.addAttribute("lastDay", lastDay);
        model.addAttribute("lastWeek", lastWeek);
        model.addAttribute("lastMonth", lastMonth);
        model.addAttribute("lastDayPercentage", lastDayPercentage);
        model.addAttribute("lastWeekPercentage", lastWeekPercentage);
        model.addAttribute("lastMonthPercentage", lastMonthPercentage);
        model.addAttribute("ownedCourses", courseOwnedDtos);
        model.addAttribute("paymentHistory", user.getPaymentHistories());
        return new ModelAndView("profil");
    }
}
