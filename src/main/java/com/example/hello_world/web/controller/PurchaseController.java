package com.example.hello_world.web.controller;

import com.example.hello_world.CourseStatus;
import com.example.hello_world.persistence.model.*;
import com.example.hello_world.persistence.repository.*;
import com.example.hello_world.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RestController
public class PurchaseController {

    private final String DISCOUNT_PATTERN = "[a-zA-Z0-9]+";

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseOwnedRepository courseOwnedRepository;

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    @Autowired
    DiscountCodeUsedRepository discountCodeUsedRepository;



    @PostMapping("/purchase/useDiscountCode")
    public Map<String, Object> useDiscountCode(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "codeName") String codeName) {
        Map<String, Object> map = new HashMap<>();

        if (courseId < 1 || courseRepository.findCourseById(courseId) == null) {
            map.put("success", false);
            map.put("message", "Nie odnaleziono takiego kursu.");
            return map;
        }


        String message = String.format("Błąd: Kod o nazwie %s nie istnieje lub utracił ważność.", codeName);

        if (codeName == null || codeName.isEmpty()) {
            map.put("success", false);
            map.put("message", "ssss");
            return map;
        }

        Pattern pattern = Pattern.compile(DISCOUNT_PATTERN);
        Matcher matcher = pattern.matcher(codeName);
        if (!matcher.matches()) {
            map.put("success", false);
            map.put("message", message);
            return map;
        }


        if (discountCodeRepository.findByName(codeName).isEmpty()) {
            map.put("success", false);
            map.put("message", "Ups. Nie znalazłem takiego kodu ;c");
            return map;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails currentUserName = (MyUserDetails) authentication.getPrincipal();
            Optional<User> user = userRepository.findByEmail(currentUserName.getUsername());
            if (user.isPresent()) {
                DiscountCode discountCode = discountCodeRepository.findByName(codeName).get();

                DiscountCodeUsed discountCodeUsed = new DiscountCodeUsed();
                discountCodeUsed.setDiscountCode(discountCode);
                discountCodeUsed.setUser(user.get());

                //Check if user already used this coupon
                /*if (discountCodeUsedRepository.findById(new DiscountCodeKey(discountCode.getId(), user.get().getId())).isPresent()) {
                    map.put("success", false);
                    map.put("message", "Użyto już tego kuponu!");
                    return map;
                }*/
                discountCodeUsed.setDate(new Date(System.currentTimeMillis()));
                discountCodeUsedRepository.save(discountCodeUsed);

                map.put("success", true);
                map.put("message", "Świetnie!");
                return map;
            }
        }


        map.put("success", true);
        map.put("message", "Wystąpił nieoczekiwany błąd ;c");
        return map;
    }




    @GetMapping("/purchase/buyCourse")
    public Map<String, Object> buyCourse() {
        Map<String, Object> map = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            MyUserDetails currentUserName = (MyUserDetails) authentication.getPrincipal();
            Optional<User> user = userRepository.findByEmail(currentUserName.getUsername());
            if (user.isPresent()) {
                Course course = courseRepository.findCourseById(1);

                CourseOwned courseOwned = new CourseOwned();
                courseOwned.setCourse(course);
                courseOwned.setUser(user.get());



                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date(System.currentTimeMillis()));
                calendar.add(Calendar.DATE, 90);
                Date expiryDate = calendar.getTime();


                courseOwned.setBuyDate(new Date(System.currentTimeMillis()));
                courseOwned.setExpiryDate(expiryDate);
                courseOwned.setStatus(CourseStatus.NEW);
                courseOwnedRepository.save(courseOwned);

                map.put("success", true);
                map.put("message", "Świetnie!");
                return map;
            }
        }


        map.put("success", true);
        map.put("message", "Wystąpił nieoczekiwany błąd ;c");
        return map;
    }
}
